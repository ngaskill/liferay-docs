# Working with the AlloyUI Project Source [](id=working-with-the-alloyui-project-source-liferay-portal-6-2-dev-guide-11-en)

You may want to work with the latest cutting-edge AlloyUI code from time to
time. Liferay makes it easy to get your hands on it. A public
[GitHub](https://github.com/) project named *alloy-ui* is used to store and 
share the latest AlloyUI code. You can download the code so that you can build 
it and try it locally. You can also leverage the alloy-ui project to create some 
AlloyUI modules of your own.

This tutorial covers the following for Liferay Portal 6.2:

- Installing the required software for the AlloyUI project
- Installing the alloy-ui project
- Building the project
- Packaging the project in a distribution and using that distribution

+$$$

**Note:** If you're trying to build a different version of Alloy other than the 
most recent, you can find the instructions in the readme file for the relevant 
branch on Github. For example, to build Alloy version 2.0 you would follow the 
instructions in the readme here:[https://github.com/liferay/alloy-ui/tree/2.0.0](https://github.com/liferay/alloy-ui/tree/2.0.0). 

$$$

You'll find out just how easy it is to install the project and use it next. Get 
started by installing AlloyUI's dependencies.

## Step 1: Setting Up AlloyUI's Required Software [](id=set-up-alloyui-required-software-liferay-portal-6-2-dev-guide-11-en)

The alloy-ui project depends on the following software:

- [Node.js](http://nodejs.org) is a platform for building applications.
- [Ruby](https://www.ruby-lang.org/en/) is used in the alloy-ui project for
  downloading other software packages. 
- [Compass](http://compass-style.org) is an open-source CSS authoring framework.
- [Sass](http://sass-lang.com/) stands for Syntactically Awesome Stylesheets. It
  is a scripting language used for specifying CSS. 

Install Node.js first. You can download it from
[http://nodejs.org/download](http://nodejs.org/download). Linux, OS X, or UNIX users can
download its source in a `.tar.gz` file, unzip it, un-tar it, and build it per
the instructions in its `README.md` file. Windows users can download the `.msi`
installer file and run it. Windows users should install Node v0.8.20 for
everything to run smoothly; using later versions can cause issues. 

---

 ![Warning](../../images/tip.png) **Warning:** On Windows, only install to
 locations that have UNIX-friendly paths. Paths like `C:\Program Files (x86)`
 that contain space characters and parentheses can prevent software from working
 properly. 

---

If you're on OS X or Linux you most likely already have Ruby installed. You can 
check by running the command `ruby -v` in your terminal. If you don't have Ruby 
installed, you can download Ruby from [https://www.ruby-lang.org](https://www.ruby-lang.org). 
Alternatively, on Windows, you can download RubyInstaller from [http://rubyinstaller.org/](http://rubyinstaller.org/) 
and use it to install Ruby. After installing Ruby, execute the following command 
from your terminal to get its latest updates:

    gem update --system

You can use Ruby's gem command to install Compass and Sass. Conveniently,
Sass comes bundled with Compass. To install both of them, run the following 
command: 

    gem install compass

Great! You've installed all of the software applications that the alloy-ui
project requires. It's time to get your hands on the alloy-ui project. 

## Step 2: Installing the AlloyUI Project [](id=install-the-alloyui-project-liferay-portal-6-2-dev-guide-11-en)

Liferay's AlloyUI developers and AlloyUI community members contribute code to
the [alloy-ui](https://github.com/liferay/alloy-ui) project on
[GitHub](https://github.com/). To access the alloy-ui project and install it
locally, you'll need an account on GitHub and the Git tool on your machine.
Visit [https://github.com/](https://github.com/) for instructions on setting up
the account and see [http://git-scm.com/](http://git-scm.com/) for instructions
on installing Git. 

Here are some simple steps for forking the alloy-ui project on GitHub and
installing the project locally: 

1.  Go to the AlloyUI project repository at
    [https://github.com/liferay/alloy-ui](https://github.com/liferay/alloy-ui).

2.  Click *Fork* to copy Liferay's alloy-ui repository to your account on
    GitHub. 

3.  In your terminal or in GitBash, navigate to the location where you want to
    put the alloy-ui project. Then download a clone of the repository by
    executing the following  command, replacing `username` with your GitHub user
    name: 

        git clone git@github.com:username/alloy-ui

    Now you have your own personal copies of the project in GitHub and on your
    local machine. Before you start building the project, you'll need to set it 
    up with the `2.5.x` branch. AlloyUI 2.5 is the version used by Liferay 
    Portal 6.2.  

4.  Navigate to your new alloy-ui directory in GitBash by running `cd alloy-ui`.

5.  To download Liferay's alloy-ui branches, you must first associate a remote
    branch to Liferay's alloy-ui repository and then fetch all of branches via
    that remote branch: 

        git remote add upstream git@github.com:liferay/alloy-ui.git
        git fetch upstream

6.  Lastly, create your own branch named `2.5.x` based on Liferay's `2.5.x`
    branch, by executing the following command:

        git checkout -b 2.5.x upstream/2.5.x

Great! Now that you have the `2.5.x` branch checked out, you can install and
initialize the project's remaining dependencies. Follow these steps:

1.  Install the global dependencies (exclude using `[sudo]` on Windows):

        [sudo] npm install -g grunt-cli shifter yogi yuidocjs phantomjs grover istanbul

2.  Then install the local dependencies:

        npm install
    
3.  Lastly, the alloy-ui project has a special target called `init` that clones
    and updates the GitHub software projects on which alloy-ui depends. These
    projects include [yui3](https://github.com/liferay/yui3.git),
    [ace-builds](https://github.com/ajaxorg/ace-builds.git),
    [alloy-bootstrap](https://github.com/liferay/alloy-bootstrap.git),
    [alloy-apidocs-theme](https://github.com/liferay/alloy-apidocs-theme.git), and
    [alloyui.com](https://github.com/liferay/alloyui.com.git).
    Initialize these projects for alloy-ui by executing this command:

        grunt init

Alright! You have the alloy-ui project and all of its dependencies. Next, you'll
build AlloyUI. 

## Step 3: Building the AlloyUI Project [](id=build-the-alloyui-project-liferay-portal-6-2-dev-guide-11-en)

The alloy-ui project contains source code for AlloyUI, YUI3, and Twitter
Bootstrap. The project uses a JavaScript build tool called Grunt to build all
kinds of things, including AlloyUI, YUI3, Twitter Bootstrap CSS, and AlloyUI API
documentation. The alloy-ui project has targets that simplify building several
sources at once and it has granular targets for building individual sets of
source code. You can view the targets on the table below.

**The alloy-ui Project Grunt Targets**

 Target  | Command | Description |
-------- | ------- | ----------- |
 `build` | `grunt build` | Builds YUI and AlloyUI together |
 `build:yui` | `grunt build:yui` | Builds YUI only |
 `build:aui` | `grunt build:aui` | Builds AlloyUI only |
 `bootstrap` | `grunt bootstrap` | Builds and imports Bootstrap's CSS |

Go ahead and build everything by executing the following command:

    grunt all

On successfully executing each of these commands, Grunt reports this message:
`Done, without errors.` Well done!

Note, to build a single AlloyUI module, you can execute the following (replace
`aui-module-name` with the module's name):

    grunt build:aui --src src/aui-module-name

You can also watch and build for any changes. Execute the following command to
build and watch for any changes overall:

    grunt watch
    
Execute the following command to watch for individual module changes:

    grunt watch --src src/aui-module-name

You can also create a new module under the `src/` folder by running the
following command:

    grunt create --name aui-module-name
    
When you're ready to try out your locally built version of AlloyUI, you can
package it up and use it. You'll do that next. 

## Step 4: Using Your Locally Built AlloyUI Distribution [](id=create-alloyui-distribution-liferay-portal-6-2-dev-guide-11-en)

Building a release distribution of your alloy-ui project is easy. And it's just
as easy using your distribution in your web pages.

To create your distribution `.zip` file of AlloyUI, execute the following
command: 

    grunt zip

This creates zip file `alloy-[version].zip`. Unzip this file to an arbitrary
location.

You can also run the following command to build modules and generate a release
zip:

    grunt release

Now that you have the AlloyUI source built locally, you can reference your 
AlloyUI distribution in your HTML files. You'll need to reference AlloyUI's 
`aui-min.js` file as a seed file.

Specify the following seed file, replacing `/home/joe.bloggs/` with the path to 
your unzipped distribution.

    <script src="/home/joe.bloggs/alloy-2.5.0/build/aui/aui-min.js"></script>

Likewise, make sure to specify your local bootstrap seed file as well:

    <link src="/home/joe.bloggs/alloy-2.5.0/build/aui-css/css/bootstrap.min.css"
      rel="stylesheet"></link>

Here is example HTML of what the seed files look like for the `aui-char-counter` 
module:

		<!DOCTYPE html>
        <html lang="en">
        <head>
          <meta charset="utf-8">
          <title>Example</title>

          <script src="/home/joe.bloggs/alloy-2.5.0/build/aui/aui-min.js"></script>
          <link href="/home/joe.bloggs/2.5.0/aui-css/css/bootstrap.min.css"
          rel="stylesheet">
        </head>
        <body>
          <input type="text" id="some-input" />
          <span id="counter"></span> character(s) remaining

          <script>
          YUI().use(
            'aui-char-counter',
            function(Y) {
              new Y.CharCounter(
                {
                  counter: '#counter',
                  input: '#some-input',
                  maxLength: 10
                }
              );
            }
          );
          </script>
        </body>
        </html>

It's just that easy to use your very own cutting-edge copy of the AlloyUI code!

## Summary [](id=alloyui-chapter-summary-liferay-portal-6-2-dev-guide-11-en)

In this tutorial, you've learned how to set up the AlloyUI project environment 
so that you can build your own AlloyUI components. You should visit AlloyUI's
official website, [http://alloyui.com/](http://alloyui.com/), regularly to get
the latest information on the AlloyUI framework.

## Related Topics

[Alloy and Bootstrap Migration](/develop/tutorials/-/knowledge_base/6-2/alloyui-2-0-taglib-and-bootstrap-migration)

[Using AlloyUI Carousel in Your Portlet](/develop/tutorials/-/knowledge_base/6-2/using-alloyui-carousel-in-your-portlet)
