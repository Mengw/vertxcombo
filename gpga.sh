#!/bin/sh

exec /usr/bin/gpg-agent --enable-ssh-support --daemon --write-env-file ${HOME}/.gpg-agent-info "$@"

