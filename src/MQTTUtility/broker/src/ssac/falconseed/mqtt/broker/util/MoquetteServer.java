/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MoquetteServer.java	0.3.0	2013/06/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MoquetteServer.java	0.1.0	2013/04/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mqtt.broker.util;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoServiceStatistics;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.demux.DemuxingProtocolDecoder;
import org.apache.mina.filter.codec.demux.DemuxingProtocolEncoder;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.dna.mqtt.commons.Constants;
import org.dna.mqtt.moquette.messaging.spi.impl.SimpleMessaging;
import org.dna.mqtt.moquette.proto.ConnAckEncoder;
import org.dna.mqtt.moquette.proto.ConnectDecoder;
import org.dna.mqtt.moquette.proto.DisconnectDecoder;
import org.dna.mqtt.moquette.proto.MQTTLoggingFilter;
import org.dna.mqtt.moquette.proto.PingReqDecoder;
import org.dna.mqtt.moquette.proto.PingRespEncoder;
import org.dna.mqtt.moquette.proto.PubAckDecoder;
import org.dna.mqtt.moquette.proto.PubAckEncoder;
import org.dna.mqtt.moquette.proto.PubCompDecoder;
import org.dna.mqtt.moquette.proto.PubCompEncoder;
import org.dna.mqtt.moquette.proto.PubCompMessage;
import org.dna.mqtt.moquette.proto.PubRecDecoder;
import org.dna.mqtt.moquette.proto.PubRecEncoder;
import org.dna.mqtt.moquette.proto.PubRelDecoder;
import org.dna.mqtt.moquette.proto.PubRelEncoder;
import org.dna.mqtt.moquette.proto.PublishDecoder;
import org.dna.mqtt.moquette.proto.PublishEncoder;
import org.dna.mqtt.moquette.proto.SubAckEncoder;
import org.dna.mqtt.moquette.proto.SubscribeDecoder;
import org.dna.mqtt.moquette.proto.UnsubAckEncoder;
import org.dna.mqtt.moquette.proto.UnsubscribeDecoder;
import org.dna.mqtt.moquette.proto.messages.ConnAckMessage;
import org.dna.mqtt.moquette.proto.messages.PingRespMessage;
import org.dna.mqtt.moquette.proto.messages.PubAckMessage;
import org.dna.mqtt.moquette.proto.messages.PubRecMessage;
import org.dna.mqtt.moquette.proto.messages.PubRelMessage;
import org.dna.mqtt.moquette.proto.messages.PublishMessage;
import org.dna.mqtt.moquette.proto.messages.SubAckMessage;
import org.dna.mqtt.moquette.proto.messages.UnsubAckMessage;
import org.dna.mqtt.moquette.server.MQTTHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>Moquette Broker</code> の実行インスタンスを保持するクラス。
 * 
 * @version 0.3.0	2013/06/30
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MoquetteServer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Logger LOG = LoggerFactory.getLogger(MoquetteServer.class);
	
	//  public static final int PORT = 1883;
	//  public static final int DEFAULT_CONNECT_TIMEOUT = 10;
	public static final String STORAGE_FILE_PATH = System.getProperty("user.home") + 
			File.separator + "moquette_store.hawtdb";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	//TODO one notifier per outbound queue, else no serialization of message flow is granted!!
	//private static final int NOTIFIER_POOL_SIZE = 1;
	private IoAcceptor	m_acceptor;
	SimpleMessaging		messaging;
	//  Thread messagingEventLoop;
	//private ExecutorService m_notifierPool/* = Executors.newFixedThreadPool(NOTIFIER_POOL_SIZE)*/;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public InetSocketAddress getDefaultSocketAddress() {
		return new InetSocketAddress(Constants.PORT);
	}

	public void startServer() throws IOException {
		DemuxingProtocolDecoder decoder = new DemuxingProtocolDecoder();
		decoder.addMessageDecoder(new ConnectDecoder());
		decoder.addMessageDecoder(new PublishDecoder());
		decoder.addMessageDecoder(new PubAckDecoder());
		decoder.addMessageDecoder(new PubRelDecoder());
		decoder.addMessageDecoder(new PubRecDecoder());
		decoder.addMessageDecoder(new PubCompDecoder());
		decoder.addMessageDecoder(new SubscribeDecoder());
		decoder.addMessageDecoder(new UnsubscribeDecoder());
		decoder.addMessageDecoder(new DisconnectDecoder());
		decoder.addMessageDecoder(new PingReqDecoder());

		DemuxingProtocolEncoder encoder = new DemuxingProtocolEncoder();
		//      encoder.addMessageEncoder(ConnectMessage.class, new ConnectEncoder());
		encoder.addMessageEncoder(ConnAckMessage.class, new ConnAckEncoder());
		encoder.addMessageEncoder(SubAckMessage.class, new SubAckEncoder());
		encoder.addMessageEncoder(UnsubAckMessage.class, new UnsubAckEncoder());
		encoder.addMessageEncoder(PubAckMessage.class, new PubAckEncoder());
		encoder.addMessageEncoder(PubRecMessage.class, new PubRecEncoder());
		encoder.addMessageEncoder(PubCompMessage.class, new PubCompEncoder());
		encoder.addMessageEncoder(PubRelMessage.class, new PubRelEncoder());
		encoder.addMessageEncoder(PublishMessage.class, new PublishEncoder());
		encoder.addMessageEncoder(PingRespMessage.class, new PingRespEncoder());

		m_acceptor = new NioSocketAcceptor();

		m_acceptor.getFilterChain().addLast( "logger", new MQTTLoggingFilter("SERVER LOG") );
		m_acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter(encoder, decoder));

		MQTTHandler handler = new MQTTHandler();
		messaging = SimpleMessaging.getInstance();
		messaging.init();
		//TODO fix this hugly wiring
		handler.setMessaging(messaging);
		//      messaging.setNotifier(handler);
		//      messagingEventLoop = new Thread(messaging);
		//      messagingEventLoop.setName("Event Loop" + System.currentTimeMillis());
		//      messagingEventLoop.start();

		m_acceptor.setHandler(handler);
		((NioSocketAcceptor)m_acceptor).setReuseAddress(true);
		((NioSocketAcceptor)m_acceptor).getSessionConfig().setReuseAddress(true);
		m_acceptor.getSessionConfig().setReadBufferSize( 2048 );
		m_acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, Constants.DEFAULT_CONNECT_TIMEOUT );
		m_acceptor.getStatistics().setThroughputCalculationInterval(10);
		m_acceptor.getStatistics().updateThroughput(System.currentTimeMillis());
		m_acceptor.bind( new InetSocketAddress(Constants.PORT) );
		LOG.info("Server binded");

//		//Bind  a shutdown hook
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			@Override
//			public void run() {
//				stopServer();
//			}
//		});
	}

	public void stopServer() {
		if (m_acceptor == null || m_acceptor.isDisposed())
			return;	// already stopped
		
		LOG.info("Server stopping...");

		messaging.stop();
		//messaging.stop();
		//      messagingEventLoop.interrupt();
		//      LOG.info("shutting down evet loop");
		//      try {
		//          messagingEventLoop.join();
		//      } catch (InterruptedException ex) {
		//          LOG.error(null, ex);
		//      }

		/*m_notifierPool.shutdown();*/

		//log statistics
		IoServiceStatistics statistics  = m_acceptor.getStatistics();
		statistics.updateThroughput(System.currentTimeMillis());
		LOG.info(String.format("Total read bytes: %d, read throughtput: %f (b/s)", statistics.getReadBytes(), statistics.getReadBytesThroughput()));
		LOG.info(String.format("Total read msgs: %d, read msg throughtput: %f (msg/s)", statistics.getReadMessages(), statistics.getReadMessagesThroughput()));

		for(IoSession session: m_acceptor.getManagedSessions().values()) {
			if(session.isConnected() && !session.isClosing()){
				session.close(false);
			}
		}

		m_acceptor.unbind();
		m_acceptor.dispose();
		
		LOG.info("Server stopped");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
