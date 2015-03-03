#!/bin/bash

ip=$(ifconfig | grep "indirizzo inet:157.27" | head -1 | cut -d':' -f2 | cut -d' ' -f1)

cd $HOME/javarmi/multe/

xterm -geometry 110x25+0+485 -T DUALSERVER -bg black -fg white -e java \
-classpath :$HOME/public_html/multe/ \
-Djava.rmi.server.hostname=$ip \
-Djava.rmi.server.codebase=http://$ip:8000/multe/ \
-Djava.security.policy=$HOME/javarmi/multe/policy/policy \
server.login.LoginServer $ip &
