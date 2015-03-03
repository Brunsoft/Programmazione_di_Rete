#!/bin/bash
ip=$(ifconfig | grep "indirizzo inet:157.27" | head -1 | cut -d':' -f2 | cut -d' ' -f1)

demone=$(pidof "rmid")
if [ $demone ]; then
	rmid -stop
	clear
	echo -e "daemon di attivazione riattivato\n"
else
	clear
	echo -e "daemon di attivazione attivato\n"
fi
rmid -log $HOME/javarmi/multe/log/ \
-J-Djava.rmi.server.codebase=file://$HOME/public_html/multe/ \
-C-Djava.rmi.dgc.leaseValue=60000 \
-J-Djava.security.policy=$HOME/javarmi/multe/policy/rmid.policy &
