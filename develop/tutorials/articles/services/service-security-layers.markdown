# Service Security Layers

Liferay's remote services sit behind a layer of security that by default allows
only local connections. Access to the remote APIs must be enabled as a separate
step in order to call them from a remote machine. Liferay's core web services
require user authentication and authentication verification. Lastly, regardless 
of whether the remote service is called from the same machine or via a web 
service, Liferay's standard security model comes into action: a user must have 
the proper permissions in Liferay's permissions system to access remote 
services. 

## IP Permission Layer

The first layer of security a client encounters when calling a remote service
is called *invoker IP filtering*. Imagine you have a batch job that runs on 
another machine in your network. This job polls a shared folder on your network 
and uploads documents to your site's *Documents and Media* portlet on a regular 
basis, using Liferay's web services. To get your batch job through the IP 
filter, the portal administrator has to allow the machine on which the batch job 
is running access to Liferay's remote service. For example, if your batch job 
uses the SOAP web services to upload the documents, the portal administrator 
must add the IP address of the machine on which the batch job is running to the
`axis.servlet.hosts.allowed` property. A typical entry might look like this:

    axis.servlet.hosts.allowed=192.168.100.100, 127.0.0.1, [SERVER_IP]

If the IP address of the machine on which the batch job is running is listed
as an authorized host for the service, it's allowed to connect to Liferay's web
services, pass in the appropriate user credentials, and upload the documents. 

---

 ![Note](../../images/tip-pen-paper.png) **Note:** The `portal.properties` file
 resides on the portal host machine and is controlled by the portal
 administrator. Portal administrators can configure security settings for the
 Axis Servlet, the Liferay Tunnel Servlet, the Spring Remoting Servlet, the JSON
 Servlet, the JSON Web Service Servlet, and the WebDAV Servlet. The
 `portal.properties` file (online version is available at
 [http://docs.liferay.com/portal/6.2/propertiesdoc/portal.properties.html](http://docs.liferay.com/portal/6.2/propertiesdoc/portal.properties.html))
 describes these properties. 

---

Next, you'll learn about the layer of security used for web services.

## Authentication/Verification Layer

If you're invoking the remote service via web services (e.g., JSON WS, old
JSON, Axis, REST, etc.), a two step process of authentication and authentication
verification is involved. Each call to a Liferay portal web service must be
accompanied by a user authentication token. It's up to the web service caller to
produce the token (e.g., through Liferay's utilities or through some third-party
software). Liferay verifies that there is a Liferay user that matches the token.
If the credentials are invalid, the web service invocation is aborted.
Otherwise, processing enters into Liferay's user permission layer. 

## User Permission Layer

Liferay's user permission layer is the last Liferay security layer triggered
when services are invoked remotely, and it's used for every object in the
portal, whether accessing it locally or remotely. The user ID accessing the
services remotely must have the proper permission to operate on the objects it's
trying to access. A remote exception is thrown if the user ID isn't permitted.
A portal administrator can grant users access to these resources. For example,
suppose you created a Documents and Media Library folder called *Documents* in a
site, created a role called *Document Uploaders*, and granted this role the
rights to add documents to your new folder. If your batch job accesses Liferay's
web services to upload documents into the folder, you have to call the web
service using a user ID of a member of this role (or using the user ID of a user
with individual rights to add documents to this folder, such as a portal
administrator). If you don't, Liferay denies you access to the web service. 

When it comes to security, password protection is very important. Next, you'll
learn how password policies can make life a little bit easier for your portal
administrator.

## Password Policies 

Your Liferay Portal password policies (see the [User
Management](https://www.liferay.com/documentation/liferay-portal/6.2/user-guide/-/ai/management-liferay-portal-6-2-user-guide-16-en)
chapter of *Using Liferay Portal 6.2*) should be reviewed, since they'll be
enforced on your administrative ID as well. If the portal is enforcing password
policies on its users (e.g., requiring them to change their passwords on a
periodic basis), an administrative ID accessing Liferay's web services in a
batch job will have its password expire too.

To prevent a password from expiring, a portal administrator can add a new
password policy that doesn't enforce password expiration and add a specific
administrative user ID to it. Then your batch job can run as many times as you
need it to, without your administrative ID's password expiring.

## Next Steps 

[Platform Frameworks](https://www-ldn.liferay.com/develop/tutorials/-/knowledge_base/platform-frameworks-lp-6-2-develop-tutorial)

[Localization](https://www-ldn.liferay.com/develop/tutorials/-/knowledge_base/localization-lp-6-2-develop-tutorial)

<!-- 
URL below needs changed to proper LDN URL when moved to LDN
-->

<!--
[Setting the Authentication type](https://www-ldn.liferay.com/develop/tutorials/-/knowledge_base/setting-authentication-type)
-->
