#!/bin/bash

export PS1="\n\e[0;36m[\t]\e[0m \e[0;35m\u\e[0m@\e[4;31m\h\e[0m:\e[1;33m\w\e[0m\e[0;32m$(git branch 2> /dev/null | sed -e '/^[^*]/d' -e 's/* \(.*\)/<\1>/')\e[0m\n→ "

