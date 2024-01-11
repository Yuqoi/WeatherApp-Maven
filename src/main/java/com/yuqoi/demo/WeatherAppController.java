package com.yuqoi.demo;

import com.yuqoi.demo.utils.Weather;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;

public class WeatherAppController implements Initializable {
    @FXML
    TextField cityNameField;

    // images
    @FXML
    ImageView weatherIcon;

    // labels
    @FXML
    Label cityName;
    @FXML
    Label tempText;
    @FXML
    Label precText;

    // values that will be updated from class
    @FXML
    Label tempValue;
    @FXML
    Label precValue;

    // buttons
    @FXML
    Button findCityBtn;
    @FXML
    Button exitBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tempText.setText("");
        precText.setText("");
        cityName.setText("");
        exitBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.exit(0);
            }
        });
    }

    @FXML
    public void findCity(ActionEvent actionEvent) {
        findCityBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                cityName.setText(cityNameField.getText());
                try {
                    Weather w = new Weather(cityNameField.getText());
                    // set temperature text and precipitation text
                    tempText.setText("Temperature : ");
                    precText.setText("Precipitation : ");

                    // set color of temperature
                    Color color = Weather.getTemperatureLevel(w.getTemperature());

                    // set icon for weather
                    URL url = getClass().getResource("images/icons/"+Weather.getWeatherIcon(w.getWeatherCode()));

                    weatherIcon.setImage(new Image(String.valueOf(url)));

                    // asign value
                    tempValue.setTextFill(color);
                    tempValue.setText(String.valueOf(w.getTemperature()) + " " + "°C");
                    precValue.setText(String.valueOf(w.getPrecipitation()) + " " + "mm");
                } catch (IOException | ParseException | org.json.simple.parser.ParseException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
    }
}