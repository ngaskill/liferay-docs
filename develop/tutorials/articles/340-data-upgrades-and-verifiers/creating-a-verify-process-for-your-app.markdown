# Creating A Verify Process for Your App [](id=creating-a-verify-process-for-your-app)

During the cycle of development, you'll want to verify the data your app is 
producing is consistent and accurate. A verify process is a class that runs 
when Liferay or your application starts. You use it to verify that your app
is functioning properly according to its business logic and report any issues
if it's not. You can do anything from sending a notification of business logic 
data integrity problems to stopping the server if there's an issue.

Verifiers should only verify that your app is in working order, and they should
never upgrade the database. Your app's data should only be updated in upgrade 
processes. You can learn more about upgrade processes in the
[Creating an Upgrade Process for Your App](/develop/tutorials/-/knowledge_base/7-0/creating-an-upgrade-process-for-your-app) 
tutorial.

This tutorial covers these topics:

- How to create a verify process for your app using new development patterns
- How to throw an exception for your verifiers
- How to execute a verify process automatically and using the Gogo shell

You can get started by creating your verify package next.

## Declaring Verifiers

To create a verify process, you must first create a package for it. Follow these
steps to create the package and classes for your verify process:

1.  Create a package called `verify` in your project's layout. 

2.  Inside the `verify` package, create an OSGi Component of the service 
    `VerifyProcess` class that extends the interface `VerifyProcess`:

        @Component(
                immediate = true,
                property = {"verify.process.name=
                com.liferay.verify.process.name"},
                service = VerifyProcess.class
        )
        public class [YourServiceNameVerifyProcess] extends VerifyProcess {
        
        ...
        
        }
 
    You must use `VerifyProcess.class` as the `service` property value to mark 
    the class as a valid implementation for the `VerifyProcess` interface. The
    `immediate` property specifies that the component starts immediately, rather
    than the first time it's used.
    
    You must also define the mandatory `verify.process.name` property, which is 
    used by the OSGi service tracker for verifiers to capture verifier 
    components.

    It is recommended that you use the name of the service package of the app as 
    the value for the `verify.process.name` property. For example, the 
    [DDMServiceVerifyProcess](https://github.com/liferay/liferay-portal/blob/master/modules/apps/forms-and-workflow/dynamic-data-mapping/dynamic-data-mapping-service/src/main/java/com/liferay/dynamic/data/mapping/verify/DDMServiceVerifyProcess.java) 
    uses the configuration shown below for the Dynamic Data Mapping app:

        @Component(
                immediate = true,
                property = {"verify.process.name=
                com.liferay.dynamic.data.mapping.service"},
                service = VerifyProcess.class
        )
        public class DDMServiceVerifyProcess extends VerifyProcess {...

    After you set up your `-VerifyProcess` class, you must declare your 
    verifiers next. The `VerifyProcess` interface provides a `doVerify()` method 
    that handles the verifiers.
 
3. Declare your verifiers within the `doVerify()` method of your 
   `*VerifyProcess` class:
        
        @Override
        protected void doVerify() throws Exception {
                [verifyName]();
                [verifyName2]();
                [verifyName3]();
                ...
        }
 
    The `DDMServiceVerifyProcess` class uses the verifiers shown below:

        @Component(
                immediate = true,
                property = {"verify.process.name=
                com.liferay.dynamic.data.mapping.service"},
                service = VerifyProcess.class
        )
        public class DDMServiceVerifyProcess extends VerifyProcess {
        
                @Override
                protected void doVerify() throws Exception {
                        verifyStructures();
                        verifyStructureLinks();
                        verifyTemplateLinks();
    
                        verifyContents();
                }
    
        }

Now that your verifiers are declared, you're ready to write them. 

## Writing Verifiers [](id=writing-verifiers)

Verifiers do the hard work of checking your data and making sure it follows the
rules your application follows in its business logic. When a bug or a change in
your app causes those rules to be broken, you can write verifiers to check for
any problems.

For example, the Guestbook portlet(an app designed in the 
[6.2 MVC Learning Path](/develop/tutorials/-/knowledge_base/6-2/writing-a-liferay-mvc-application)
)
originally allowed users to enter a message of any length into the guestbook. 
In an updated version, a character counter was added to the app to keep the 
messages to a consistent length of no more than 140 characters. If a user enters
a message of more than 140 characters, a verifier can act as an added measure of 
security, and let the user know that their message must be no more than 140 
characters in length.

If your verifier is simple, you can write all of it inside the `*VerifyProcess`
class, but for anything but the simplest use cases, that gets clunky. Separate
the logic into different classes instead.

In this section, you'll look closer at the DDM verifier classes to learn how 
verifiers are written and organized.

The DDM service verify process consists of four verifiers: `verifyStructures()`, 
which verifies that the ddm form and layout is configured correctly; 
`verifyStructureLinks()`, which verifies the ddm structure link ID; 
`verifyTemplateLinks()`, which verifies the ddm template ID; and 
`verifyContents()`, which verifies that the ddm content contains valid data.

If you look closer at the `verifyStructures()` verifier, you can see how
verifiers are organized. The `verifyStructures()` verifier is broken down into 
two methods: `verifyDDMForm()`, which checks that the default language of each 
`DDMForm` is present as an available locale in the `DDMForm`, and also that all 
the form fields are compliant with the app's business logic; and 
`verifyDDMFormLayout()`, which checks that there are no duplicated field names 
in the `DDMForm`, if the title of all pages is the same as the `DDMFormLayout` 
language, and that all columns in each page's rows do not have a size greater 
than 12.

Below is a fragment of the `DDMServiceVerifyProcess` class with a focus on the 
code related to the `verifyStructures()` verifier:

    import com.liferay.dynamic.data.mapping.validator.DDMFormValidator;
    
    ...

    public class DDMServiceVerifyProcess extends VerifyProcess {

    ...
    
        @Override
        protected void doVerify() throws Exception {
            verifyStructures();
            verifyStructureLinks();
            verifyTemplateLinks();
    
            verifyContents();
        }
        
        ...
    
        @Reference(unbind = "-")
                protected void setDDMFormValidator(DDMFormValidator 
                ddmFormValidator) {
                        _ddmFormValidator = ddmFormValidator;
        }
    
        ...
    
        protected void verifyDDMForm(DDMForm ddmForm) throws PortalException {
                    _ddmFormValidator.validate(ddmForm);
        }

        ...
    
        protected void verifyStructure(DDMStructure structure)
                    throws PortalException {
    
                    verifyDDMForm(structure.getDDMForm());
                    verifyDDMFormLayout(structure.getDDMFormLayout());
        }

        ...
    
        protected void verifyStructures() throws Exception {
                    try (LoggingTimer loggingTimer = new LoggingTimer()) {
                            ActionableDynamicQuery actionableDynamicQuery =
                                    _ddmStructureLocalService.getActionableDynamicQuery();
    
                            actionableDynamicQuery.setPerformActionMethod(
                                    new 
                                    ActionableDynamicQuery.PerformActionMethod() 
                                    {
    
                                            @Override
                                            public void performAction(Object 
                                            object)
                                                    throws PortalException {
    
                                                    DDMStructure structure = 
                                                    (DDMStructure)object;
    
                                                    verifyStructure(structure);
                                            }
    
                                    });
    
                            actionableDynamicQuery.performActions();
                    }
        }
    
        ...
    
    }

If you examine the code above, you'll see that the `verifyStructures()` verifier
throws portal exceptions, calling a series of other methods. Each method handles
a smaller and smaller portion of the validation.

Look at the `verifyDDMForm(structure.getDDMForm())` method for example. This
method calls the `_ddmFormValidator.validate(ddmForm)` method for validation. 
The actual implementation for this method is written within the 
[DDMFormValidatorImpl](https://github.com/liferay/liferay-portal/blob/master/modules/apps/forms-and-workflow/dynamic-data-mapping/dynamic-data-mapping-validator/src/main/java/com/liferay/dynamic/data/mapping/validator/internal/DDMFormValidatorImpl.java)
class.

Below is the `_ddmFormValidator.validate()` method for the 
`DDMFormValidatorImpl` class:

    @Component(immediate = true)
    public class DDMFormValidatorImpl implements DDMFormValidator {
    
            @Override
            public void validate(DDMForm ddmForm) throws 
            DDMFormValidationException {
                    validateDDMFormLocales(ddmForm);
    
                    List<DDMFormField> ddmFormFields = 
                    ddmForm.getDDMFormFields();
    
                    if (ddmFormFields.isEmpty()) {
                            throw new MustSetFieldsForForm();
                    }
    
                    validateDDMFormFields(
                            ddmFormFields, new HashSet<String>(), 
                            ddmForm.getAvailableLocales(),
                            ddmForm.getDefaultLocale());
    }

The `verifyDDMForm()` method's validation is defined in the `validate()` method 
above. As you can see the logic for the verifiers is spread across several 
classes.

Likewise, the layout validation, the `verifyDDMFormLayout()` method, for the 
`DDMServiceVerifyProcess` class is handled within the 
[DDMFormLayoutValidatorImpl](https://github.com/liferay/liferay-portal/blob/master/modules/apps/forms-and-workflow/dynamic-data-mapping/dynamic-data-mapping-validator/src/main/java/com/liferay/dynamic/data/mapping/validator/internal/DDMFormLayoutValidatorImpl.java) 
implementation class.

What you do in your verifiers depends on your requirements, but you can use the
same approach to write your verifiers. 

Once your verifiers are written, you can learn how to address any data issues
that come up next.

## Addressing Business Logic Data Integrity Issues [](id=addressing-business-logic-data-integrity-issues)

You may be wondering why all these exceptions are being thrown in the example 
code. The answer to that question gets to the heart of the verifier's purpose.
A verifier validates your app's business logic and ensures the data stored won't
cause your app any problems. In the case of a bug missing data (arising from
user input or some other cause), the verifier is meant to catch the issue and
report a problem.

In the `DDMServiceVerifyProcess` class example, the app throws exceptions when 
there is an issue with a verify process.

When issues occur with your app's business logic, you'll need to communicate 
the issues to your users. You could write the issue to a log or even throw
an exception if it's a critical issue that should stop the application or
@product@.

Throwing an exception, in most cases, is the best approach to communicate your 
app's data integrity issues. This ensures that the portal will only start when 
your app is in full working order.

Follow these steps to write exceptions for your app:

1.  First, create a class for your exception that extends the `PortalException`
    class.  This class can hold all the exceptions for your app.

2.  Next, if you want to follow Liferay's pattern, define specific exceptions
    related to your business logic as inner subclasses of the `*Exception` class
    you just created:
    
        public class [ExceptionClassName] extends PortalException {
            public static class [SpecificExceptionName] extends [ExceptionClassName] {
                ...
            }
        }
 
    When defining your exceptions, it's best to give them concrete names that
    clearly communicate the reason for the exception.
    
    For example, the `DDMFormValidationException` class declares its exceptions
    as subclasses within its class: 
    
        public static class MustSetDefaultLocale
                        extends DDMFormValidationException {
            ...
        }
 
    The exception name `MustSetDefaultLocale` makes it clear that you must 
    set a default locale to solve the error.
 
3.  Next, define the constructors for your exception subclass.

    For example, the `DDMFormValidationException` class defines the 
    `MustSetDefaultLocale()` method this way:

        public static class MustSetDefaultLocale
                        extends DDMFormValidationException {
                        
            public MustSetDefaultLocale() {
                    super("The default locale property was not set 
                    for the DDM form");
            }
        
        }
 
    In this case, you can see that the constructor uses the `super` method to 
    call the superclass's constructor (`DDMFormValidationException`) 
    to set the error message for the exception. You can use the 
    same pattern to write your error messages.

    Once your exceptions are declared, you can call the exceptions in your
    verifiers next.
 
4.  Open your `-VerifyProcess` implementation class, for example
    `DDMFormValidatorImpl` and throw an exception using the main `-Exception`
    class that you created in step 1.
    
    For example, the `validateDDMFormLocales(DDMFORM ddmForm)` method in the 
    `DDMFormValidatorImpl` class throws a `DDMFormValidationException` in
    its `validateDDMFormLocales()` method:

        protected void validateDDMFormLocales(DDMForm ddmForm)
                throws DDMFormValidationException {
            ...
        }
 
5.  Next, define the conditions to call the exception subclass you created in
    step 2.

    For example, the `validateDDMFormLocales(DDMFORM ddmForm)` method sets the
    following conditions for its `DDMFormValidationException`:
        
        protected void validateDDMFormLocales(DDMForm ddmForm)
                throws DDMFormValidationException {
        
                Locale defaultLocale = ddmForm.getDefaultLocale();
        
                if (defaultLocale == null) {
                    throw new MustSetDefaultLocale();
                }
        
                validateDDMFormAvailableLocales(
                    ddmForm.getAvailableLocales(), defaultLocale);
        }

In this example, if there is no default locale specified for the structure, a
new instance of `MustSetDefaultLocale()` is created and thrown, calling the
`MustSetDefaultLocale()` class method, which sets the error text.

In the case of any verifier exceptions, the server startup process is stopped. 
This ensures that if any major issues need to be addressed immediately, the 
server won't start until they are fixed.

Now that you know how to report your app's data integrity issues, you can learn 
how to declare dependencies on Liferay services for your verify processes next.

## Using Liferay Services in Your Verify Process [](id=using-liferay-services-in-your-verify-process)

Verify processes execute when a new release starts. It's important to be aware
of this while running upgrade processes because your database starts in a
previous schema version, then gets upgraded to the new schema version.

This is an important distinction between upgrade processes and verify processes.
An upgrade process of the portal core cannot use Liferay services to access the 
database, because the Liferay services use the upgraded definition of the
database. Verify processes, since they run after the upgrade, run in the
upgraded schema version, so they can use release Liferay services. You can learn
more about upgrade processes in the [Creating an Upgrade Process for Your
App](/develop/tutorials/-/knowledge_base/7-0/creating-an-upgrade-process-for-your-app) 
tutorial.

If you choose to use Liferay services in your verify process, you must declare
dependencies on those services. Because the services are not available until the
core, or the modules they rely on, is started, **your verify process must
depend on them to load** before your processes can execute.

Follow these steps to declare dependencies on Liferay services for your verify 
process:

1.  Use the `@Reference` annotation to declare a dependency for your verify 
    process.

    For example, here is a reference to the `DLFileVersionLocalService`
    for the [Documents and Media Library app](https://github.com/liferay/liferay-portal/blob/2960360870ae69360861a720136e082a06c5548f/modules/apps/collaboration/document-library/document-library-service/src/main/java/com/liferay/document/library/workflow/DLFileEntryWorkflowHandler.java):

        @Reference(unbind = "-")
        protected void setDLFileVersionLocalService(
          DLFileVersionLocalService dlFileVersionLocalService) {
    
          _dlFileVersionLocalService = dlFileVersionLocalService;
        }
        ...
        private DLFileVersionLocalService _dlFileVersionLocalService;

This asks the OSGi framework to provide a valid implementation of the proper
service (in this example, a `DLFileVersionLocalService`). When an implementation
is available, the verify process can use it.

The `ServiceUtil` classes are provided for backwards compatibility, but you
should not use them. Instead, use the OSGi framework to provide the Liferay
services. Then you can take advantage of the framework's ability to manage
service dependencies. 

Now that your dependencies are declared, your next step is to execute your
verifiers.

## Executing your Verify Process [](id=executing-your-verify-process)

Once the verifiers for your app are written, executing them is a breeze.

To execute your verify process, deploy the module that contains it. Copy your
module's `jar` file into your app server's `deploy` folder, under 
`LIFERAY_HOME`. 

The verify process is executed immediately.

You may wish to execute your verifiers more than just when they are deployed. 
You can learn how to execute your verifiers using the Gogo shell next. 

### Executing your Verify Process Using the Gogo Shell [](id=executing-your-verify-process-using-the-gogo-shell)

You can trigger a new execution of any verify process already present in the
portal. Using the [Gogo shell](/develop/reference/-/knowledge_base/7-0/using-the-felix-gogo-shell), the
OSGi command line shell, you can perform the following useful operations over
the existing verify processes:

 - **execute:** Executes the verify process passed as an argument. For example,
 you would run the following command from the Gogo shell to execute the 
 `com.liferay.journal.service` module's verify process: 
 `g! verify:execute com.liferay.journal.service`.
 
 - **executeAll:** Executes all deployed verify processes, using 
 the following command from the Gogo shell: `g! verify:executeAll`.
 
 - **list:** Lists all deployed verify processes, using
 the following command from the Gogo shell: `g! verify:list`. This produces
 output like this:
 
    Registered verify process com.liferay.portal.scheduler.internal.verify
    Registered verify process com.liferay.portal.security.sso.cas
    Registered verify process com.liferay.journal.service
    Registered verify process com.liferay.portal.security.sso.openid
    Registered verify process com.liferay.dynamic.data.mapping.service
    Registered verify process com.liferay.portal.security.sso.token.siteminder
    Registered verify process com.liferay.wiki.service
    Registered verify process com.liferay.portal.security.sso.ntlm
    Registered verify process com.liferay.portal.security.sso.token.shibboleth
    Registered verify process com.liferay.polls.service
    Registered verify process com.liferay.portal.security.sso.google
    Registered verify process com.liferay.mobile.device.rules.service
    Registered verify process com.liferay.shopping.service
    Registered verify process com.liferay.portal.security.ldap
    Registered verify process com.liferay.portal.security.sso.opensso
    Registered verify process com.liferay.portal.security.service.access.policy.service
    Registered verify process com.liferay.document.library.service
    Registered verify process com.liferay.portal.security.sso.facebook.connect
    Registered verify process com.liferay.bookmarks.service
 
 - **show:** Shows information about the verify process passed as an argument.
 For example, you would run the following command in the Gogo shell to show
 information about the `com.liferay.portal.security.sso.cas` module's verify
 process: `g! verify:show com.liferay.portal.security.sso.cas`. This produces
 output like this:
 
    Registered verify process com.liferay.portal.security.sso.cas
 
There you go. Now you know how to create a verification process for your app!

## Related Topics [](id=related-topics)

[Creating an Upgrade Process for Your App](/develop/tutorials/-/knowledge_base/7-0/creating-an-upgrade-process-for-your-application)

[Migrating a Liferay 6 Application](/develop/tutorials/-/knowledge_base/7-0/migrating-a-liferay-6-application)

[Application Configuration](/develop/tutorials/-/knowledge_base/7-0/application-configuration)
