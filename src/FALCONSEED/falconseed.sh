#! /bin/sh

#
#  FALCON-SEED Launcher 1.00
#

# ----------------------
#  Options
# ----------------------

# 'JAVAVM_OPTIONS' is JavaVM option for java command.
JAVAVM_OPTIONS=

# 'LANGUAGE' is locale for FALCON-SEED. 'ja' or 'en' can be specified for this option. 
LANGUAGE=

# ----------------------
#  Command
# ----------------------

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home
           fi
           ;;
esac
### debug
#echo [Debug] OS Specific support.
#echo     cygwin=$sygwin
#echo     darwin=$darwin
### debug

# APP_HOME
APP_HOME=
if [ -z "$APP_HOME" -o ! -d "$APP_HOME" ] ; then
  ## resolve links - $0 may be a link to Application's home
  PRG="$0"
  progname=`basename "$0"`

  # need this for relative symlinks
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
    else
    PRG=`dirname "$PRG"`"/$link"
    fi
  done

  APP_HOME=`dirname "$PRG"`

  # make it fully qualified
  APP_HOME=`cd "$APP_HOME" && pwd`
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
  [ -n "$APP_HOME" ] &&
    APP_HOME=`cygpath --unix "$APP_HOME"`
fi
### debug
#echo [Debug] APP_HOME specification.
#echo     APP_HOME=$APP_HOME
### debug

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java 2> /dev/null `
    if [ -z "$JAVACMD" ] ; then
        JAVACMD=java
    fi
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi

# For Cygwin, switch paths to appropriate format before running java
# For PATHs convert to unix format first, then to windows format to ensure
# both formats are supported. Probably this will fail on directories with ;
# in the name in the path. Let's assume that paths containing ; are more
# rare than windows style paths on cygwin.
if $cygwin; then
  if [ "$OS" = "Windows_NT" ] && cygpath -m .>/dev/null 2>/dev/null ; then
    format=mixed
  else
    format=windows
  fi
  APP_HOME=`cygpath --$format "$APP_HOME"`
  JAVA_HOME=`cygpath --$format "$JAVA_HOME"`
  CYGHOME=`cygpath --$format "$HOME"`
fi

# add a second backslash to variables terminated by a backslash under cygwin
if $cygwin; then
  case "$APP_HOME" in
    *\\ )
    APP_HOME="$APP_HOME\\"
    ;;
  esac
  case "$CYGHOME" in
    *\\ )
    CYGHOME="$CYGHOME\\"
    ;;
  esac
  case "$JIKESPATH" in
    *\\ )
    JIKESPATH="$JIKESPATH\\"
    ;;
  esac
fi
# Execute ant using eval/exec to preserve spaces in paths,
# java options, and ant args
aadl_sys_opts=
if [ -n "${CYGHOME}" ]; then
  if [ -n "${JIKESPATH}" ]; then
    app_sys_opts="-Djikes.class.path=\"${JIKESPATH}\" -Dcygwin.user.home=\"${CYGHOME}\""
  else
    app_sys_opts="-Dcygwin.user.home=\"${CYGHOME}\""
  fi
else
  if [ -n "${JIKESPATH}" ]; then
    app_sys_opts="-Djikes.class.path=\"${JIKESPATH}\""
  fi
fi
if [ -n "${LANGUAGE}" ] ; then
  opt_applang="-language ${LANGUAGE}"
fi
exec_command="exec \"${JAVACMD}\" ${JAVAVM_OPTIONS} ${app_sys_opts} -jar \"${APP_HOME}/FALCONSEED.jar\" ${opt_applang}"
### debug
#echo $exec_command
### debug
eval $exec_command

