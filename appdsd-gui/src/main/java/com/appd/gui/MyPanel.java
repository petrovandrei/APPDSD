package com.appd.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTable;

import com.appd.entity.Sensor;
import com.appd.enumeration.RequestSender;
import com.appd.enumeration.RequestTypes;
import com.appd.exception.BadVersionException;
import com.appd.exception.NoConnectionException;
import com.appd.util.JsonUtil;
import com.appd.util.UtilGui;




public class MyPanel extends JPanel implements MouseListener {
	BufferedImage img1;
	Image title;
	Map<Rectangle, Integer> rectangle;

	private Point position;
	private Point mouse;

	UtilGui utilGui = new UtilGui();
	JsonUtil jsonUtil = new JsonUtil();

	TextArea comboSensors = new TextArea();
	List<Sensor> sensorsFoundList = new ArrayList<Sensor>();
	private JTable sensorsTable = new JTable();
	private String reponse;

	private JPanel sensorsLocated = new JPanel();

	public MyPanel(BufferedImage img1, Image title, Map<Rectangle, Integer> listRectangle,
			 TextArea comboSensors) {
		this.img1 = img1;
		this.title = title;
		this.rectangle = listRectangle;

		//this.sensorsTable = sensorsTable;
		this.comboSensors = comboSensors;
		this.addMouseListener(this);

		this.add(sensorsLocated);
		this.add(sensorsTable);

		
	}

	@Override
	public Dimension getPreferredSize() {
		return img1 == null ? new Dimension(50, 50) : new Dimension(

				img1.getWidth(), img1.getHeight());


	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		System.out.println("Rentre dans le paintComponent");
		JPanel north = new JPanel();
		JPanel south = new JPanel();


		Graphics2D g2 = (Graphics2D) g.create();
		//Graphics2D g3 = (Graphics2D) g.create();
		g2.setColor(Color.white);
		//g3.setColor(Color.white);

		//g2.drawImage(title, null, 50,100);
		//g2.drawImage(title, 50, 100, null);
		
		if (img1 != null) {


			int x = (getWidth() - img1.getWidth()) / 2;
			int y = (getHeight() - img1.getHeight()) / 2;
			g2.drawImage(img1, x, y, this);
			rectangle.forEach((rectangle, id) -> {
					g2.draw(rectangle);
					//g3.drawString(id.toString(), rectangle.x, rectangle.y);
					
				});
		}

		this.add(north, BorderLayout.NORTH);
		this.add(south, BorderLayout.SOUTH);

		g2.dispose();
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// récupérer la position du clique
		position = e.getPoint();
		System.out.println(position);

		findSensorByLocation(testLocation(position));


	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void findSensorByLocation(int locationId) {
		String empty = "Liste des capteurs présents";
		comboSensors.setText(empty);
		System.out.println(locationId);
		List<String> requestedValues = new ArrayList<String>();
		String requestedValue = String.valueOf(locationId);
		System.out.println(requestedValue);
		requestedValues.add(requestedValue);

		List<String> fields = new ArrayList<String>();
		fields.add("ID_LOCATION");
		System.out.println(fields);

		//TODO : appeler mes sensors et les afficher


		try {
			System.out.println("Entre dans le try");
			reponse = utilGui.executeRequest(jsonUtil.serializeRequestConstruct(RequestTypes.SELECT, Sensor.class, null, fields, requestedValues, null, RequestSender.CLIENT));
			System.out.println(reponse);
		} catch (NoConnectionException e) {
			System.out.println("catch1");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("catch2");
			e.printStackTrace();
		} catch (BadVersionException e) {
			System.out.println("catch3");
			e.printStackTrace();
		}

		//TODO : display sensors
			List<String> sensorsList = (List<String>) JsonUtil.deserializeJsonObjectToJavaObjet(reponse);
		String affiche = "Liste des capteurs présents";
		int newLineIndicator = 1;
		if(sensorsList != null && sensorsList.size() > 0)
		{
			for(String s : sensorsList)
			{
				affiche += newLineIndicator + s + "\n";

				newLineIndicator++;
			}
		}
		comboSensors.setText(affiche);



	}

	private Integer testLocation(Point mouse) {
		Integer locationId = null;

		for (Map.Entry<Rectangle, Integer> entry : rectangle.entrySet()) {

			if (entry.getKey().getBounds().contains(mouse)) {

				locationId = entry.getValue();
				System.out.println(locationId);
			}

		}

		return locationId;

	}



	

}
