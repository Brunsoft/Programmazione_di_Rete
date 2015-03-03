#!/bin/bash
clear
if [ $1 ]; then

ip=$(ifconfig | grep "indirizzo inet:157.27" | head -1 | cut -d':' -f2 | cut -d' ' -f1)
	
SERVERIP=$1
LOCALIP=$ip
CODEBASE=http://$1:8000/multe/
export SERVERIP
export LOCALIP

cp conf/iiop.policy policy/iiop.policy
# imposto il protocollo file
sed -i 's,_CLIENTPROT,'file://',g' $PWD/policy/iiop.policy
# imposto il path dove risiedono i file del client
sed -i 's,_CLIENTPATH,'$PWD/-',g' $PWD/policy/iiop.policy
# Imposto l'IP del Server
sed -i 's,_SERVERIP,'$SERVERIP',g' $PWD/policy/iiop.policy
# Imposto il Codebase
sed -i 's,_CODEBASE,'$CODEBASE-',g' $PWD/policy/iiop.policy

java \
-Djava.security.policy=./policy/iiop.policy \
-Djava.rmi.server.codebase=$CODEBASE \
-Djava.rmi.server.hostname=$1 \
-Djavax.net.ssl.trustStore=./cert/Server_Truststore \
-Djavax.net.ssl.trustStorePassword=servermulte \
client.bootstrap.ClientIIOPBootstrap $1

else
	echo -e "\033[36;5mQuesto client va lanciato come ./Client.sh IP_SERVER:PORTA\n(Esempio ./CLient.sh 157.27.241.001)\033[0m"
fi

