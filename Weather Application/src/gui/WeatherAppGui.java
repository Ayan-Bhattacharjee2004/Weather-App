package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONObject;

import backend.WeatherApp;
import gui.constants.CommonConstants;


public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui() {
        // setup our gui and thr title
        super("Weather App :)");

        // set Color
       getContentPane().setBackground(CommonConstants.PRIMARY_COLOR);

        // set the size of the gui
        setSize(450, 650);

        // configure the gui to end the program's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // load our gui at the center of the screen
        setLocationRelativeTo(null);

        // prevent any resize of our gui
        setResizable(false);

        // make our layout manager null to manually position our components within the
        // gui
        setLayout(null);

        addGuiComponents();
    }

    private void addGuiComponents() {
        // search field
        JTextField searchTextField = new JTextField();

        // set the location and size of our component
        searchTextField.setBounds(15, 15, 351, 45);

        // change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("D:\\Java\\Java Project\\Projectes\\Weather Application\\src\\assets\\cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
    
        // temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        //set color
       temperatureText.setForeground(CommonConstants.Text_COLOR);

        // center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);

        // weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        /* set color */
        weatherConditionDesc.setForeground(CommonConstants.Text_COLOR);

        // humidity image
        JLabel humidityImage = new JLabel(loadImage("Weather Application\\src\\assets\\humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);

        // humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        // set color
        humidityText.setForeground(CommonConstants.Text_COLOR);

        // windspeed image
        JLabel windspeedImage = new JLabel(loadImage("Weather Application\\src\\assets\\Windspeed.png"));
        windspeedImage.setBounds(220, 497, 74, 66);

        // windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        // set color
        windspeedText.setForeground(CommonConstants.Text_COLOR);

        /* search button */
        JButton searcgButton = new JButton(loadImage("Weather Application\\src\\assets\\Search1.png"));
        searcgButton.setBounds(375, 13, 47, 45);
        searcgButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // get location from user
                String userInput = searchTextField.getText();
                // validate input - remove whitespace to ensure non-empty text
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }
                // retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);
                // update gui

                // update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");
                // depending on the condition, we will update the weather image that corresponds
                // with the condition
                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("Weather Application\\src\\assets\\clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("Weather Application\\src\\assets\\cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("Weather Application\\src\\assets\\rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("Weather Application\\src\\assets\\snow.png"));
                        break;
                }

                // update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                // update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                // update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }

        });

        /* Add the components */
        add(searchTextField);
        add(searcgButton);
        add(weatherConditionImage);
        add(temperatureText);
        add(weatherConditionDesc);
        add(humidityImage);
        add(humidityText);
        add(windspeedImage);
        add(windspeedText);
    }

    // use to create images in our gui components
    private ImageIcon loadImage(String resourcePath) {
        try {
            /* read the image file form the path given */
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // return an image icon so that our component can render it
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Could not  find resource");
        return null;
    }
}
