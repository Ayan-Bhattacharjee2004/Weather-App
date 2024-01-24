import javax.swing.SwingUtilities;

import backend.WeatherApp;
import gui.WeatherAppGui;

public class App {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){
                //deisplay our weather app gui
                new WeatherAppGui().setVisible(true);
                //testing: location geographical data form the API
               // System.out.println(WeatherApp.getLocationData("Kolkata"));
               // System.out.println(WeatherApp.getCurrentTime());
               
                
            }
        });
    }
}

