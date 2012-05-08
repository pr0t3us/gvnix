rm -rf /tmp/gvnixtest*
mkdir /tmp/gvnixtest${BUILD_NUMBER}
cd /tmp/gvnixtest${BUILD_NUMBER}
${WORKSPACE}/code/bin/gvnix-dev script --file clinic.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file vote.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file expenses.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file gwtNoEntities.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file wedding.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-web-report/src/main/resources/report.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-web-report/src/test/resources/gvnix-test.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/src/main/assembly/samples/gvnix-sample.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/src/test/resources/script.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/src/test/resources/tiendavirtual.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-web-theme/src/main/resources/theme.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-web-i18n/src/main/resources/es-i18n.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-dynamic-configuration/src/test/resources/test.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-occ/src/main/resources/occ.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-web-menu/src/main/resources/menu.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-web-menu/src/test/resources/base.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-service/src/main/resources/bing.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-service/src/main/resources/service.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-service/src/test/resources/gvnix-test-no-web.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-service/src/test/resources/gvnix-test.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/spring-flex-roo/org.springframework.flex.roo.addon/src/test/resources/basic-flex-scaffold-test.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/spring-flex-roo/org.springframework.flex.roo.addon/src/test/resources/remoting-scaffold-all-test.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/spring-flex-roo/rootunes.roo
rm -rf /tmp/gvnixtest${BUILD_NUMBER}/*
${WORKSPACE}/code/bin/gvnix-dev script --file ${WORKSPACE}/code/addon-web-pattern/src/test/resources/test-script-roo
startx -- `which Xvfb` :1 -screen 0 1024x768x24 2>&1 >/dev/null &
export DISPLAY=:1
mvn tomcat:run selenium:xvfb selenium:selenese tomcat:stop -Dmaven.tomcat.fork=true