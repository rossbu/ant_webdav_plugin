# Webdav client by Ant

## Author
Tianshi Bu

## Usage
```xml
  <echo>Start - downloading Ant Artifacts...... </echo>
  <mkdir dir="${user.home}/.ant/lib"/>
  <get src="https://seapi-3rdparty.s3.amazonaws.com/jenkins/antplugins/commons-codec-1.3.jar" dest="${user.home}/.ant/lib/commons-codec-1.3.jar"/>
  <get src="https://seapi-3rdparty.s3.amazonaws.com/jenkins/antplugins/commons-httpclient-3.1.jar" dest="${user.home}/.ant/lib/commons-httpclient-3.1.jar"/>
  <get src="https://seapi-3rdparty.s3.amazonaws.com/jenkins/antplugins/commons-logging-1.2.jar" dest="${user.home}/.ant/lib/commons-logging-1.2.jar"/>
  <get src="https://seapi-3rdparty.s3.amazonaws.com/jenkins/antplugins/jackrabbit-webdav-2.5.1.jar" dest="${user.home}/.ant/lib/jackrabbit-webdav-2.5.1.jar"/>
  <get src="https://seapi-3rdparty.s3.amazonaws.com/jenkins/antplugins/slf4j-api-1.6.4.jar" dest="${user.home}/.ant/lib/slf4j-api-1.6.4.jar"/>
  <get src="https://seapi-3rdparty.s3.amazonaws.com/jenkins/antplugins/webdav-client-anttasks-0.5.jar" dest="${user.home}/.ant/lib/webdav-client-anttasks-0.5.jar"/>
  <echo>Done - Ant Artifacts are downloaded to ${user.home}...... </echo>
  Then

  Execute script as below
        <taskdef resource="com/smartequip/webdav/client/webdav-tasks.xml"/>
        <davClient  xmlns:davClient="antlib:WebdavTasks"  username="xxx" password="yyy">
             <delete url="${DEPLOYTOURL}/${envname}/"/>
             <!--url should not end with / -->
             <put url="${DEPLOYTOURL}">
                 <fileset dir="." casesensitive="yes" includes="${envname}/" />
             </put>
        </davClient>
```




