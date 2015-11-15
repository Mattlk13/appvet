AppVet GWT GUI Development


AppVet uses the Google Web Toolkit (GWT) to develop and render the AppVet
GUI. To facilitate design and development of AppVet GUIs, it is recommended to 
use the GWT Designer which provides a WYSIWYG GUI editor and is part of the GWT 
package bundle. Note that AppVet GUIs have been designed and tested using 
GWT Designer with the following specific configuration:

* Windows 7 64 bit
* Oracle JDK 7
* GWT 2.6.1
* Eclipse Kepler

Note that GWT Designer may not work properly if used with a different
OS, JDK, GWT, or Eclipse configuration. Further note that GWT Designer may not 
work on Linux due to library incompatibilities of the GWT Window Builder on 
Linux. In such cases, it is recommended to modify the GUI using GWT Designer 
under Windows and then commit the changes to GIT/SVN so that they can be 
pulled onto, and used by, the AppVet development environment on Linux.

