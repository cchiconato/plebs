package br.com.intro;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.intro.utils.AppContext;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		new Timer().scheduleAtFixedRate(new TimerTask(){
		    @Override
		    public void run(){
		       System.out.println("Request for heroku no sleeping after 30 min");
		    }
		},0, 29*60000);
		SpringApplication.run(AppContext.class, args);
	}

}
