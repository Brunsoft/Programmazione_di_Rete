#!/bin/bash
#cancella registri rmi se esistenti
rmi=$(pidof "rmiregistry")
if [ `pidof "rmiregistry" | cut -d" " -f1` ]; then
	kill -9 $rmi
fi
#cancella python se esistente
python=$(pidof "python")
if [ `pidof "python" | cut -d" " -f1` ]; then
	kill -9 $python
fi
#cancella tnameserv se esistente
tnameserv=$(pidof "tnameserv")
if [ `pidof "tnameserv" | cut -d" " -f1` ]; then
  	kill -9 $tnameserv
fi
#cancella java se esistente
java=$(pidof "java")
if [ `pidof "java" | cut -d" " -f1` ]; then
  	kill -9 $java
fi

ip=$(ifconfig | grep "indirizzo inet:157.27" | head -1 | cut -d':' -f2 | cut -d' ' -f1)

#	CANCELLO VECCHI FILE E CARTELLE
clear
if [ -d $HOME/public_html/multe/ ]; then
	rm -r $HOME/public_html/multe/
fi

#	RICREO LE DIRECTORY MANCANTI
mkdir $HOME/public_html/multe/
mkdir $HOME/public_html/multe/server/
mkdir $HOME/public_html/multe/server/central/
mkdir $HOME/public_html/multe/server/login/
mkdir $HOME/public_html/multe/client/
mkdir $HOME/public_html/multe/client/bootstrap/
mkdir $HOME/public_html/multe/client/login/
mkdir $HOME/public_html/multe/client/mobileA/
mkdir $HOME/public_html/multe/client/mobileS/
mkdir $HOME/public_html/multe/lib/
chmod 777 $HOME/public_html/multe/

echo -e "\033[36;5mIl server verra' lanciato con python \n Il mio ip e' $ip\033[0m"
cd $HOME/javarmi/multe/

#	COMPILAZIONE
javac \
Setup.java \
lib/Multa.java \
lib/Ref.java \
client/bootstrap/ClientJRMPBootstrap.java \
client/bootstrap/ClientIIOPBootstrap.java \
client/login/AutenticatoreJRMP.java \
client/login/AutenticatoreIIOP.java \
client/mobileA/Autovelox.java \
client/mobileA/MobileAgent.java \
client/mobileS/Caserma.java \
client/mobileS/MobileServer.java \
server/central/CentralServer.java \
server/central/ICentralServer.java \
server/central/ICAutovelox.java \
server/central/ICCaserma.java \
server/login/ILogin.java \
server/login/LoginServer.java \
server/login/ILCaserma.java \
server/login/ILAutovelox.java \
server/login/ILoginServer.java -Xlint

rmic -d $HOME/public_html/multe/ server.central.CentralServer
rmic -d $HOME/public_html/multe/ server.login.LoginServer
rmic -d $HOME/public_html/multe/ client.mobileS.Caserma
rmic -iiop -d $HOME/public_html/multe/ server.login.LoginServer

# 	SPOSTAMENTO .CLASS NELLE DIRECTORY CONDIVISE

cp server/central/ICentralServer.class $HOME/public_html/multe/server/central/
cp server/central/CentralServer.class $HOME/public_html/multe/server/central/
cp server/central/ICCaserma.class $HOME/public_html/multe/server/central/
cp server/central/ICAutovelox.class $HOME/public_html/multe/server/central/
cp server/login/LoginServer.class $HOME/public_html/multe/server/login/
cp server/login/LoginServer\$CountingReference.class $HOME/public_html/multe/server/login/
cp server/login/ILogin.class $HOME/public_html/multe/server/login/
cp server/login/ILoginServer.class $HOME/public_html/multe/server/login/
cp server/login/ILCaserma.class $HOME/public_html/multe/server/login/
cp server/login/ILAutovelox.class $HOME/public_html/multe/server/login/
cp lib/Multa.class $HOME/public_html/multe/lib/
cp lib/Ref.class $HOME/public_html/multe/lib/
cp client/bootstrap/ClientJRMPBootstrap.class $HOME/public_html/multe/client/bootstrap/
cp client/bootstrap/ClientIIOPBootstrap.class $HOME/public_html/multe/client/bootstrap/
cp client/login/AutenticatoreJRMP.class $HOME/public_html/multe/client/login/
cp client/login/AutenticatoreIIOP.class $HOME/public_html/multe/client/login/
cp client/mobileA/Autovelox.class $HOME/public_html/multe/client/mobileA/
cp client/mobileA/MobileAgent.class $HOME/public_html/multe/client/mobileA/
cp client/mobileS/Caserma.class $HOME/public_html/multe/client/mobileS/
cp client/mobileS/MobileServer.class $HOME/public_html/multe/client/mobileS/
cp client/mobileS/Caserma\$hidePanel.class $HOME/public_html/multe/client/mobileS/

chmod 777 $HOME/public_html/multe/ *.class

echo -e "Lancia il demone d'attivazione da un'altra finestra, quando hai fatto premi invio.\n"
read invio

unset CLASSPATH
rmiregistry -J-Djava.rmi.server.codebase=file:///home/luca/public_html/multe/ &
rmiregistry 2222 -J-Djava.rmi.server.codebase=file:///home/luca/public_html/multe/ &
#rmiregistry 2378 -J-Djava.rmi.server.codebase=file:///home/dalmonte/public_html/common/ &
tnameserv -ORBInitialPort 5555 &> /dev/null &
cd $HOME/public_html/
xterm -geometry 110x25+0+0 -T PYTHON -bg black -fg white -e python -m SimpleHTTPServer &
cd $HOME/javarmi/multe/

xterm -geometry 110x25+0+485 -T DUALSERVER -bg black -fg white -e java \
-classpath :$HOME/public_html/multe/ \
-Djava.rmi.server.hostname=$ip \
-Djava.rmi.server.codebase=http://$ip:8000/multe/ \
-Djava.security.policy=$HOME/javarmi/multe/policy/setup.policy \
-Djava.rmi.dgc.leaseValue=60000 \
server.login.LoginServer $ip &

java \
-classpath :$HOME/public_html/multe/ \
-Djava.rmi.server.codebase=http://$ip:8000/multe/ \
-Djava.rmi.server.hostname=$ip \
-Dmulte.impl.codebase=http://$ip:8000/multe/ \
-Djava.security.policy=$HOME/javarmi/multe/policy/setup.policy \
-Dmulte.classeserver=server.central.CentralServer \
-Dmulte.policy=$HOME/javarmi/multe/policy/group.policy \
-Djava.rmi.dgc.leaseValue=60000 \
-Dmulte.ip=$ip \
Setup &

#java -classpath :$HOME/public_html/common/ -Djava.rmi.server.hostname=$ip -Djava.rmi.server.codebase=http://$ip:8000/common/ -Djava.security.policy=$HOME/javarmi/multe/policy multe.BootstrapServer &

#java -classpath :$HOME/public_html/common/ -Djava.rmi.server.hostname=$ip -Djava.rmi.server.codebase=http://$ip:8000/common/ -Djava.security.policy=$HOME/javarmi/multe/policy multe.ServerMulteImpl

