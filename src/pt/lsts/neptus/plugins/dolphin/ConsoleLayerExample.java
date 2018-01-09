package pt.lsts.neptus.plugins.dolphin;

import pt.lsts.neptus.console.ConsoleLayer;
import pt.lsts.neptus.loader.NeptusMain;
import pt.lsts.neptus.plugins.PluginDescription;
import pt.lsts.neptus.plugins.PluginsRepository;

@PluginDescription(name="Example Console Layer")
public class ConsoleLayerExample extends ConsoleLayer {

	@Override
	public void cleanLayer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initLayer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean userControlsOpacity() {
		// TODO Auto-generated method stub
		return false;
	}
	
	// How to test your plug-ins in debug mode.
	// If running inside Eclipse, set the "Working directory" as the Neptus directory.
	public static void main(String[] args) {
		PluginsRepository.addPlugin(ConsoleLayerExample.class.getName());
		PluginsRepository.addPlugin(DolphinConsolePanel.class.getName());
		NeptusMain.main(args);
	}
}
