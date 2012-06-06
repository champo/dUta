#!/bin/bash

set -e

./add-filter deny-url "google" ip "127.0.0.1"
read -p "Press any key to continue... "

curl -vvv -x 127.0.0.1:9999 www.google.com
read -p "Press any key to continue... " 

curl -vvv -x 127.0.0.1:9999 www.google.com.ar
read -p "Press any key to continue... " 

curl -vvv -x 127.0.0.1:9999 www.facebook.com/google
read -p "Press any key to continue... " 

curl -vvv -x 127.0.0.1:9999 www.facebook.com
read -p "Press any key to continue... " 

./delete-filter
