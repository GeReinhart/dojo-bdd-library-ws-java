#!/bin/bash

echo "Your machine should use http://geppetto.infra.kelkoo.net/namespaces/dev/tags/trunk/nodes/dojo_bdd_suggestions_ws"
sudo rm -rf /opt/yum_repository/RPMS/noarch/*
mvn clean package -DskipTests
createrepo -d /opt/yum_repository/RPMS/noarch/
sudo punch-restore 

echo "Start the tomcat with 'sudo -u kookel /etc/init.d/tomcat_dojosuggestionsws start'"
echo "Then go to http://localhost:8888/dojosuggestionsws/application.wadl"

