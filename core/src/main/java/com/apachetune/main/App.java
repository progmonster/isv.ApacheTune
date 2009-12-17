package com.apachetune.main;

import com.apachetune.core.*;
import com.google.inject.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

// TODO FIX Maximizing persist MainFrame logic.
// TODO Add a status bar.
// TODO Add an initial page into mainframe when no page was opened.
// TODO Remove method run and runOrShow from controllers. Replace it to specific action methods!
// TODO Change the author's email to progmonster@gmail.com
// TODO Configure a continue integration.
// TODO Create update system
// TODO Create donate offer (pay now small and have unbounded license for all versions)
// TODO Create feedback system
// TODO Create "before release" indicator with any pomps.
// TODO First production version will be 1.0.0-alpha and will be for free (with the donate offer).
// TODO Configure pom-files properly in all project's modules.
// TODO Show dialog on application closing (It should be configurable).
// TODO Should It use XUL or another UI markup language for creating UI (particularly MenuBar, ToolBar)?
// TODO Add a syntax colorer configurator with a load/save color scheme feature. Create some color schemes (yellow-blue
//  for turbo-pascal's developers, green-black for unix's terminal fans, etc.) 
/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class App {
    private static final String CONFIG_FILE_NAME = "config.xml";

    public static void main(String[] args) throws Exception {
        new App();
    }

    // TODO Show an error dialog when errors.
    public App() throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException,
            IllegalAccessException, InstantiationException {        
        MainModuleController mainModuleController = new MainModuleController();

        String[] moduleCtrlsClassNames = getModuleControllersClassNames();

        List<Module> modules = new ArrayList<Module>(moduleCtrlsClassNames.length + 1);

        modules.add(mainModuleController.getModule());

        List<ModuleController> moduleControllers = new ArrayList<ModuleController>(moduleCtrlsClassNames.length);
                                                  
        for (String ctlrClassName : moduleCtrlsClassNames) {
            ModuleController controller = (ModuleController) Class.forName(ctlrClassName).newInstance();

            Module module = controller.getModule();                                

            if (module != null) {
                modules.add(module);
            }

            moduleControllers.add(controller);
        }

        Injector injector = Guice.createInjector(modules);

        injector.injectMembers(mainModuleController);

        mainModuleController.initialize(null);

        RootWorkItem rootWorkItem = mainModuleController.getRootWorkItem();

        for (ModuleController controller : moduleControllers) {
            injector.injectMembers(controller);

            controller.initialize(rootWorkItem);
        }

        rootWorkItem.initialize();
    }

    private String[] getModuleControllersClassNames() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document config = db.parse(getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));

        NodeList modulesNodes = config.getDocumentElement().getElementsByTagName("module");

        String[] result = new String[modulesNodes.getLength()];

        for (int moduleIdx = 0; moduleIdx < modulesNodes.getLength(); moduleIdx++) {
            Element moduleElement = (Element) modulesNodes.item(moduleIdx);

            String moduleControllerClassName = moduleElement.getAttribute("moduleControllerClass");

            result[moduleIdx] = moduleControllerClassName;
        }

        return result;
    }
}
