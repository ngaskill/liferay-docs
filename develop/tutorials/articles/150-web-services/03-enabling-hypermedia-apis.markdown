# Enabling Hypermedia REST APIs [](id=enabling-hypermedia-rest-apis)

Liferay's hypermedia REST APIs are based on two different components:
-  APIO architect, the Liferay's module that supports the implementation of 
   hypermedia REST APIs.
- APIs implementation, which contains the code for the specific APIs endpoints.

APIO architect is included in Liferay CE Portal 7.1 GA2 and Liferay DXP 7.1 
Fix Pack 1. To use these APIs in earlier 7.1 releases, you must install it manually.
With APIO

1.  Remove the Pre-installed APIO Architect module.
2.  Install the Latest Version of APIO
3.  Enable Access to the APIs
4.  Install the APIs implementation

Steps 1 to 3 are only required if you are running Liferay CE Portal 7.1 GA1.
The last step is necessary for all the versions. 

The following sections walk you through these steps. 

## Remove the Pre-installed APIO Architect module [](id=remove-pre-installed-apio-architect)

You must first remove the older, pre-installed version of the APIO Architect module: 

1.  Navigate to `[Liferay Home]/osgi/marketplace`. The 
    [Liferay Home](/discover/deployment/-/knowledge_base/7-1/installing-liferay#liferay-home) 
    folder is typically the application server's parent folder. 

2.  Delete the APIO packages. For example, here are the APIO packages for 
    Liferay Portal: 

        Liferay CE Foundation - Liferay CE APIO Architect - API.lpkg 
        Liferay CE Foundation - Liferay CE APIO Architect - Impl.lpkg

    If the portal is running, you should see in the console that the modules in 
    those packages have been stopped: 

        2018-07-16 12:42:22.186 INFO  [fileinstall-$LIFERAY_HOME/osgi/marketplace][BundleStartStopLogger:38] STOPPED Liferay CE Foundation - Liferay CE Apio Architect - API_1.0.0 [387]
        2018-07-16 12:42:22.199 INFO  [fileinstall-$LIFERAY_HOME/osgi/marketplace][BundleStartStopLogger:38] STOPPED com.liferay.apio.architect.api_1.0.2 [388]
        2018-07-16 12:42:22.216 INFO  [Refresh Thread: Equinox Container: c0a2f090-f388-0018-1c45-fc3bc84c1049][BundleStartStopLogger:38] STOPPED com.liferay.apio.architect.uri.mapper.impl_1.0.0 [715]
        2018-07-16 12:42:22.323 INFO  [Refresh Thread: Equinox Container: c0a2f090-f388-0018-1c45-fc3bc84c1049][BundleStartStopLogger:38] STOPPED com.liferay.apio.architect.impl_1.0.1 [714]
        2018-07-16 12:42:22.335 INFO  [Refresh Thread: Equinox Container: c0a2f090-f388-0018-1c45-fc3bc84c1049][BundleStartStopLogger:38] STOPPED com.liferay.apio.architect.exception.mapper.impl_1.0.1 [713]
        2018-07-16 12:42:22.347 INFO  [fileinstall-$LIFERAY_HOME/osgi/marketplace][BundleStartStopLogger:38] STOPPED Liferay CE Foundation - Liferay CE Apio Architect - Impl_1.0.0 [712]

3.  Use the 
    [Felix Gogo shell](/develop/reference/-/knowledge_base/7-1/using-the-felix-gogo-shell) 
    to verify that the bundles have been removed. To do so, run this command in 
    the Gogo shell: 

        lb apio

    If this returns results for the `*apio*` bundles, then you must delete them
    via the Gogo shell. Take note of each bundle's ID and run `uninstall
    [BUNDLE_ID]` for each. For example, if the bundle IDs were 388, 713, 714,
    and 715, then you would run these commands in the Gogo shell: 

        uninstall 388
        uninstall 713
        uninstall 714
        uninstall 715

4.  Finally, remove the `*apio*` configuration file in 
    `[Liferay Home]/osgi/configs`. 

## Install the Latest Version of APIO [](id=install-the-latest-version-of-apio)

Now you must download and install the latest version of the APIO, the modules that support Hypermedia REST APIs in @product@: 

1.  Download the updated APIO modules by clicking the link for each: 

    -   [`com.liferay.apio.architect.api-1.5.0.jar`](http://central.maven.org/maven2/com/liferay/com.liferay.apio.architect.api/1.5.0/com.liferay.apio.architect.api-1.5.0.jar)
    -   [`com.liferay.apio.architect.impl-1.0.9.jar`](http://central.maven.org/maven2/com/liferay/com.liferay.apio.architect.impl/1.0.9/com.liferay.apio.architect.impl-1.0.9.jar)
    -   [`com.liferay.apio.architect.uri.mapper.impl-1.0.1.jar`](http://central.maven.org/maven2/com/liferay/com.liferay.apio.architect.uri.mapper.impl/1.0.1/com.liferay.apio.architect.uri.mapper.impl-1.0.1.jar)
    -   [`com.liferay.apio.architect.exception.mapper.impl-1.0.3.jar`](http://central.maven.org/maven2/com/liferay/com.liferay.apio.architect.exception.mapper.impl/1.0.3/com.liferay.apio.architect.exception.mapper.impl-1.0.3.jar) 

2.  Deploy these files to the `[Liferay Home]/deploy` folder. The console should 
    show that the modules are starting: 

        2018-07-16 13:01:26.477 INFO  [com.liferay.portal.kernel.deploy.auto.AutoDeployScanner][AutoDeployDir:261] Processing com.liferay.apio.architect.api-1.5.0.jar
        2018-07-16 13:01:26.483 INFO  [com.liferay.portal.kernel.deploy.auto.AutoDeployScanner][AutoDeployDir:261] Processing com.liferay.apio.architect.impl-1.0.9.jar
        2018-07-16 13:01:26.484 INFO  [com.liferay.portal.kernel.deploy.auto.AutoDeployScanner][AutoDeployDir:261] Processing com.liferay.apio.architect.exception.mapper.impl-1.0.3.jar
        2018-07-16 13:01:26.484 INFO  [com.liferay.portal.kernel.deploy.auto.AutoDeployScanner][AutoDeployDir:261] Processing com.liferay.apio.architect.uri.mapper.impl-1.0.1.jar
        2018-07-16 13:01:31.818 INFO  [fileinstall-$LIFERAY_HOME/osgi/modules][BundleStartStopLogger:35] STARTED com.liferay.apio.architect.exception.mapper.impl_1.0.3 [948]
        2018-07-16 13:01:31.898 INFO  [fileinstall-$LIFERAY_HOME/osgi/modules][BundleStartStopLogger:35] STARTED com.liferay.apio.architect.impl_1.0.9 [949]
        2018-07-16 13:01:32.831 INFO  [fileinstall-$LIFERAY_HOME/osgi/modules][BundleStartStopLogger:35] STARTED com.liferay.apio.architect.api_1.5.0 [947]
        2018-07-16 13:01:32.839 INFO  [fileinstall-$LIFERAY_HOME/osgi/modules][BundleStartStopLogger:35] STARTED com.liferay.apio.architect.uri.mapper.impl_1.0.1 [950]

    The modules should then appear in `[Liferay Home]/osgi/modules`. 

## Enable Access to the APIs [](id=enable-access-to-the-apis)

By default, APIO security restricts access to the APIs. To enable access, you must 
add a specific configuration file: 

1.  Create the file 
    `com.liferay.apio.architect.impl.application.ApioApplication-default.config` 
    with these contents: 

        auth.verifier.auth.verifier.BasicAuthHeaderAuthVerifier.urls.includes="*"
        auth.verifier.auth.verifier.OAuth2RestAuthVerifier.urls.includes="*"
        auth.verifier.guest.allowed="true"
        oauth2.scopechecker.type="none"

2.  Deploy the file to `[Liferay Home]/osgi/configs`. 

3.  Once the OSGi container loads the configuration, you can make a request to
    the API's root URL:

        curl http://localhost:8080/o/api

    This should give you an empty root response: 

        {
            "@id":"http://localhost:8080/o/api",
            "@type":"EntryPoint",
            "@context":[  
            {  
                "@vocab":"http://schema.org/"
            },
            "https://www.w3.org/ns/hydra/core#"
            ]
        }

    If you instead receive a permissions error, then the configuration didn't 
    load. In this case, try restarting the portal. 

## Install the APIs implementation [](id=install-apis-impl)

Once that you have the right version of Apio Architect module installed, you
need to:

1. Download the Hypermedia REST APIs zip from marketplace and unzip its
   contents on a temp folder.

2. Copy all the *.jar files to $LIFERAY_HOME/deploy folder. You will see in 
   the log that a bunch of modules with the implementation of the APIs are 
   installed and started.

        2018-08-28 12:54:16.635 INFO  [BundleStartStopLogger:35] STARTED com.liferay.portal.apio.impl_1.0.6 [978]
        2018-08-28 12:54:16.640 INFO  [BundleStartStopLogger:35] STARTED com.liferay.role.apio.api_1.0.1 [979]
        2018-08-28 12:54:16.669 INFO  [BundleStartStopLogger:35] STARTED com.liferay.site.apio.impl_1.0.5 [982]
        2018-08-28 12:54:16.719 INFO  [BundleStartStopLogger:35] STARTED com.liferay.workflow.apio.impl_1.0.5 [996]
        2018-08-28 12:54:16.754 INFO  [BundleStartStopLogger:35] STARTED com.liferay.web.page.element.apio.impl_1.0.5 [992]
        2018-08-28 12:54:16.757 INFO  [BundleStartStopLogger:35] STARTED com.liferay.keyword.apio.api_1.0.1 [965]
        2018-08-28 12:54:16.780 INFO  [BundleStartStopLogger:35] STARTED com.liferay.layout.apio.impl_1.0.3 [968]
        2018-08-28 12:54:16.784 INFO  [BundleStartStopLogger:35] STARTED com.liferay.category.apio.api_1.0.1 [953]
        2018-08-28 12:54:16.786 INFO  [BundleStartStopLogger:35] STARTED com.liferay.workflow.apio.api_1.1.3 [995]
        2018-08-28 12:54:16.927 INFO  [BundleStartStopLogger:35] STARTED com.liferay.category.apio.impl_1.0.9 [954]
        2018-08-28 12:54:16.994 INFO  [BundleStartStopLogger:35] STARTED com.liferay.folder.apio.impl_1.0.4 [962]
        2018-08-28 12:54:16.997 INFO  [BundleStartStopLogger:35] STARTED com.liferay.vocabulary.apio.api_1.0.1 [989]
        2018-08-28 12:54:17.006 INFO  [BundleStartStopLogger:35] STARTED com.liferay.forms.apio.api_1.1.0 [963]
        2018-08-28 12:54:17.009 INFO  [BundleStartStopLogger:35] STARTED com.liferay.organization.apio.api_1.0.1 [971]
        2018-08-28 12:54:17.047 INFO  [BundleStartStopLogger:35] STARTED com.liferay.phone.apio.impl_1.0.1 [976]
        2018-08-28 12:54:17.049 INFO  [BundleStartStopLogger:35] STARTED com.liferay.web.page.element.apio.api_1.0.1 [991]
        2018-08-28 12:54:17.101 INFO  [BundleStartStopLogger:35] STARTED com.liferay.media.object.apio.impl_1.0.9 [970]
        [...]

3. Copy the 
   com.liferay.apio.architect.impl.application.ApioApplication-default.config 
   file to $LIFERAY_HOME/osgi/config

4. Wait a few seconds until the config file is reloaded by the OSGi container 
   and try it by making a request to the the API's root URL:

        curl http://localhost:8080/o/api

    You should get a response with the root resources available with the API
    implementation:

        {
        "@id" : "http://localhost:8080/o/api",
        "collection" : [ {
            "@id" : "http://localhost:8080/o/api/p/person",
            "manages" : {
            "property" : "rdf:type",
            "object" : "schema:Liferay:UserAccount"
            },
            "@type" : [ "Collection" ]
        }, {
            "@id" : "http://localhost:8080/o/api/p/content-space",
            "manages" : {
            "property" : "rdf:type",
            "object" : "schema:ContentSpace"
            },
            "@type" : [ "Collection" ]
        }, {
            "@id" : "http://localhost:8080/o/api/p/web-site",
            "manages" : {
            "property" : "rdf:type",
            "object" : "schema:WebSite"
            },
            "@type" : [ "Collection" ]
        }, {
            "@id" : "http://localhost:8080/o/api/p/organization",
            "manages" : {
            "property" : "rdf:type",
            "object" : "schema:Organization"
            },
            "@type" : [ "Collection" ]
        }, {
            "@id" : "http://localhost:8080/o/api/p/roles",
            "manages" : {
            "property" : "rdf:type",
            "object" : "schema:Role"
            },
            "@type" : [ "Collection" ]
        } ],
        "@type" : "EntryPoint",
        "@context" : [ {
            "@vocab" : "http://schema.org/"
        }, "https://www.w3.org/ns/hydra/core#" ]
        }


## Related Topics [](id=related-topics)

[Consuming Web Services](/develop/tutorials/-/knowledge_base/7-1/consuming-web-services)