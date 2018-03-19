# Neptus plugin for Dolphin


## Requirements
* Java Development Kit (JDK) 8
* Neptus
* Apache ANT  

## Compilation instructions

* Compile / Install Neptus in your machine.
* Edit the file named **plugin.properties** with the settings for your plug-in.
* Optionally generate an eclipse project by issuing the command:
   * `ant GenerateEclipseProject`
* After creating your plug-in you can deploy it to Neptus using the *deploy* ant target:
   * `ant deploy`
* To test the plug-in in Neptus<sup>1</sup> run the command:
   * `ant test`

<sup>1</sup>  The plug-in must be added to the operators console through the Plugin Manager (`Ctrl + P` or View >> Plugin Manager) look for `Dolphin Runtime Feature` on the list and add it).
