#!/bin/bash

find . -name "*.asc" -print -exec gpg --verify {} \;
