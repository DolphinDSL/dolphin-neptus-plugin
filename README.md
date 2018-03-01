# Dolphin Neptus plugin


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

