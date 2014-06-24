# Setting the Authentication Type

The authentication type specified for your Liferay Portal instance dictates the 
authentication type you'll use to access your web service. The portal 
administrator can set the portal's authentication type to any of the following: 

- *email address*
- *screen name*
- *user ID*

---

 ![Tip](../../images/tip-pen-paper.png) **Important:** In order for
 authentication to work for remote service calls, the portal authentication type
 must be set either to *screen name* or *user ID*. Authentication using the
 *email address* authentication type is not supported for remote service calls. 

---

You can set the authentication type via the Control Panel or via the
`portal-ext.properties` file.

## Setting Authentication Via the Control Panel

To set the portal authentication type via the Control Panel use the following 
steps:

1. Navigate to the Control Panel.

2. Click on *Portal Settings*, and then on *Authentication*. 

3. Under *How do users authenticate?*, make a selection.

That's all there is to it. Easy, right?

## Setting Authentication Via the portal-ext.properties File

To set the portal authentication type via properties file, add the following
lines to your Liferay instance's `portal-ext.properties` file and uncomment the
line for the appropriate authentication type:

    #company.security.auth.type=emailAddress
    #company.security.auth.type=screenName
    #company.security.auth.type=userId

Now that you know how to set your authentication type, it's time to test it 
next.

## Calling the AXIS Web Service

With remote services, you can specify the user credentials using HTTP basic
authentication. Since those credentials are passed over the network unencrypted,
it is recommended that you use HTTPS whenever accessing these services on an 
untrusted network. Most HTTP clients let you specify the basic authentication 
credentials in the URL--this is very handy for testing.

Use the following syntax to call the AXIS web service using credentials. Make
sure to remove the line escape character `\` when entering your URL:

    http://" + screenNameOrUserIdAsString + ":" + password + "@[server.com]:\
    [port]/api/axis/" + serviceName

The `screenNameOrUserIdAsString` should either be the user's screen name or the 
user's ID from the Liferay database. As mentioned above, the portal's 
authentication type setting determines which one to use. 
    
For example if your user ID is `2` and your password is `test`, you would access 
Liferay's remote Organization service with the following URL: 

    http://2:test@localhost:8080/api/axis/Portal_OrganizationService

---

 ![Note](../../images/tip-pen-paper.png) **Note:**  A user can find his or her 
 ID by logging in as the user and accessing *My Account* from the Dockbar. On 
 this interface, the user ID appears below the user's profile picture and above 
 the birthday field.

---

Using the proper credentials based on the authentication type you set, you will 
see a text that reads:

    *Portal_OrganizationService*

    Hi there, this is an AXIS service! ...

Congrats, you have successfully set your authentication type and tested it out!

## Next Steps
<!-- URL will need replaced with proper URL when moved to LDN-->
 <!--[Using Liferay's SOAP web services](https://www-ldn.liferay.com/develop/tutorials/-/knowledge_base/using-soap-web-services)--> 
