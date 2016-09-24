To support enhanced customization for organizational deployments, AppVet supports
organization-specific logos that may be used. These logos include:

* appvet_logo_login.png: The organization-specific AppVet logo for the login page and About dialog box.
* org_logo_login.png: The organization's logo for the login page.
* appvet_logo_main.png: The organization-specific AppVet logo for the main AppVet page.
* org_logo_main.png: The organization's logo for the main AppVet page.

To use these images in your deployment of AppVet, edit these (.png) image files for your specific organization using an appropriate photo editing program then copy the modified files to your (Tomcat) 
$CATALINA_HOME/webapps/appvet_images directory. 

To ensure the correct operation and appearance of AppVet, it is necessary to retain the name of the 
images as well as their sizes. The following are the image names and sizes used by AppVet:

* appvet_logo_login.png (192px x 73px)
* org_logo_login.png (125px x 125px)
* appvet_logo_main.png (379px x 47px)
* org_logo_main.png (296px x 87px)

If your image sizes differ, AppVet will fit your images into the sizes defined above.

The favicon.ico file is the web browser icon used for AppVet. This file should be placed in the 
$CATALINA_HOME/webapps/ROOT directory and override the default favicon.ico file in this directory.

