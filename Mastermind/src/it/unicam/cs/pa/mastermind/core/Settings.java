package core;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Properties;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;


public class Settings {
	private Properties settings;
	private static Settings instance;
	private JFrame frame;
	private JSlider nPegs,nRounds,boardSize,codeSize;
	private JCheckBox empty,codeBreaker;
	private JComboBox<String> pegType,p1,p2;
	private JRadioButton GUI,textUI,local,server,client;
	private String[] types = {"Colors","Letters","Digits"};
	private String[] opponents = {"Knuth","Random","Human","Genetic"};
	private int res = JOptionPane.OK_OPTION;

	private void setSlider(JSlider tmp, int spacing)
	{
		tmp.setMajorTickSpacing(spacing);
		tmp.setMinorTickSpacing(spacing);
		tmp.setPaintTicks(true);
		tmp.setPaintLabels(true);
	}
	
	private void pegTypeHandler(ItemEvent e)
	{
		if(e.getStateChange()==ItemEvent.SELECTED)
			{
				switch(e.getItem().toString())
				{
					case "Colors": nPegs.setMaximum(8); break;
					case "Letters": nPegs.setMaximum(6); break;
					case "Digits": nPegs.setMaximum(10); break;
				}
			}
	}

	private int alert()
	{
		return JOptionPane.showConfirmDialog(frame, "It is not recommended to use Knuth with codes longer than 4 or more peg types than 6!","Warning", JOptionPane.OK_CANCEL_OPTION);
	}
	private synchronized void startHandler(ActionEvent e)
	{
		if((p1.getSelectedItem().toString() == "Knuth" || p2.getSelectedItem().toString() == "Knuth") && (codeSize.getValue()>4 || nPegs.getValue()>6))
			res = alert();
		else
			res = JOptionPane.OK_OPTION;
		if(res != JOptionPane.CANCEL_OPTION)
		{
			settings = new Properties();
			settings.setProperty("pegs", Integer.toString(nPegs.getValue()));
			settings.setProperty("Rounds", Integer.toString(nRounds.getValue()));
			settings.setProperty("BoardSize", Integer.toString(boardSize.getValue()));
			settings.setProperty("CodeSize", Integer.toString(codeSize.getValue()));
			settings.setProperty("EmptyAllowed", Boolean.toString(empty.isSelected()));
			settings.setProperty("CodeBreaker", Boolean.toString(codeBreaker.isSelected()));
			settings.setProperty("PegType", pegType.getSelectedItem().toString());
			settings.setProperty("Player1", p1.getSelectedItem().toString());
			settings.setProperty("Player2", p2.getSelectedItem().toString());
			settings.setProperty("UI", GUI.isSelected()?"gui":"TextUI");
			settings.setProperty("Match", local.isSelected()?"Local": server.isSelected() ? "Server" : "Client");
			frame.dispose();
			this.notify();
		}

	}
	
	private JPanel makeSliders()
	{
		//number of pegs
		nPegs = new JSlider(JSlider.HORIZONTAL,
				1, 8, 6);
		setSlider(nPegs,1);
		//number of rounds
		nRounds = new JSlider(JSlider.HORIZONTAL,
				2, 10, 4);
		setSlider(nRounds,2);
		//size of board
		boardSize = new JSlider(JSlider.HORIZONTAL,
				1, 10, 8);
		setSlider(boardSize,1);
		//size of code
		codeSize = new JSlider(JSlider.HORIZONTAL,
				1, 10, 4);
		setSlider(codeSize,1);

		JPanel sliders = new JPanel();
		sliders.setLayout(new BoxLayout(sliders,BoxLayout.Y_AXIS));
		sliders.add(new JLabel("Number of different pegs"));
		sliders.add(nPegs);
		sliders.add(new JLabel("Number of rounds"));
		sliders.add(nRounds);
		sliders.add(new JLabel("Max number of guesses"));
		sliders.add(boardSize);
		sliders.add(new JLabel("Size of hidden code"));
		sliders.add(codeSize);
		return sliders;
	}
	
	private JPanel makeChecks()
	{
		//empty allowed
		empty = new JCheckBox();
		//start as codeBreaker
		codeBreaker = new JCheckBox();

		JPanel checks = new JPanel();
		checks.setLayout(new GridLayout(2,2));
		checks.add(new JLabel("Allow empty spaces in code"));
		checks.add(empty);
		checks.add(new JLabel("Player 1 starts as CodeBreaker"));
		checks.add(codeBreaker);
		return checks;
	}
	
	private JPanel makeCombos()
	{
		//type of pegs
		pegType = new JComboBox<>(types);
		pegType.setSelectedIndex(0);
		pegType.addItemListener(this::pegTypeHandler);

		//p1
		p1 = new JComboBox<>(opponents);
		p1.setSelectedIndex(2);

		//p2
		p2 = new JComboBox<>(opponents);
		p2.setSelectedIndex(0);

		JPanel dropdown = new JPanel();
		dropdown.setLayout(new BoxLayout(dropdown,BoxLayout.Y_AXIS));
		dropdown.add(new JLabel("Select type of pegs"));
		dropdown.add(pegType);
		dropdown.add(new JLabel("Select Player1"));
		dropdown.add(p1);
		dropdown.add(new JLabel("Select Player2"));
		dropdown.add(p2);
		return dropdown;
	}
	
	private JPanel makeRadios()
	{
		//gui or text
		GUI = new JRadioButton("Graphical User Interface");
		GUI.setSelected(true);
		textUI = new JRadioButton("Textual User Interface ( Open the program by terminal! )");
		ButtonGroup UI = new ButtonGroup();
		UI.add(GUI);
		UI.add(textUI);

		//local or online
		local = new JRadioButton("Play locally");
		local.setSelected(true);
		server = new JRadioButton("Create server");
		client = new JRadioButton("Play as client");
		ButtonGroup loc = new ButtonGroup();
		loc.add(local);
		loc.add(server);
		loc.add(client);


		JPanel select = new JPanel();
		select.setLayout(new BoxLayout(select,BoxLayout.Y_AXIS));
		select.add(new JLabel("Select UI"));
		select.add(textUI);
		select.add(GUI);
		select.add(new JLabel("Match type"));
		select.add(local);
		select.add(server);
		select.add(client);
		return select;
	}
	
	private JPanel rightSide()
	{
		JPanel dropdown = this.makeCombos();
		JPanel checks = this.makeChecks();
		JPanel selectUI = this.makeRadios();
		JPanel rightSide = new JPanel();
		rightSide.setLayout(new BoxLayout(rightSide,BoxLayout.Y_AXIS));
		dropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
		checks.setAlignmentX(Component.LEFT_ALIGNMENT);
		selectUI.setAlignmentX(Component.LEFT_ALIGNMENT);
		rightSide.add(dropdown);
		rightSide.add(Box.createVerticalStrut(10));
		rightSide.add(selectUI);
		rightSide.add(Box.createVerticalStrut(10));
		rightSide.add(checks);
		return rightSide;
	}
	private void settingsGUI()
	{	
		JPanel main = new JPanel();
		main.setLayout(new GridBagLayout());
		JButton start = new JButton("Start");
		start.addActionListener(this::startHandler);
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.gridx = 0;
		c.gridy = 0;
		main.add(this.makeSliders(),c);
		
		c.gridx = 1;
		c.gridy = 0;
		main.add(this.rightSide(),c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 2;
		main.add(start,c);

		frame = new JFrame();
		frame.add(main);
		frame.pack();
		frame.setVisible(true);
		
	}

	private Settings()
	{
		this.settingsGUI();
	}
	private Settings(Properties p)
	{
		this.settings = p;
	}
	private Properties getProperties()
	{
		return this.settings;
	}
	public static Properties getGameProperties()
	{	
		if(instance==null) {
			instance=new Settings();
			try {
				synchronized(instance) {
				instance.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return instance.getProperties();
		}else
			return instance.getProperties();
		
	}
	
	public static void setGameProperties(Properties p)
	{
		if(instance==null)
			instance = new Settings(p);
		else throw new RuntimeException("Game Properties already been set!");
	}

}
