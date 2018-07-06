package gui;


import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import core.Code;
import core.CodePeg;
import core.InfoManager;
import core.Outcome;
import core.Settings;
import core.Status;
import players.HumanGUIPlayer;

@SuppressWarnings("deprecation")
public class GUI extends ObserverGUI implements Observer{
	private boolean codeBreaker;
	private HumanGUIPlayer player;
	private ExecutorService executor;
	private int pegs;
	private JButton send;
	private LineBorder highlight = new LineBorder(Color.red);
	private boolean amIP1;

	public GUI(boolean codeBreaker, Map<String,CodePeg> pegSet)
	{
		super(pegSet);
		this.pegs = Integer.parseInt(Settings.getGameProperties().getProperty("pegs"));
		super.getToolkit().addAWTEventListener(this::mousePressed, AWTEvent.MOUSE_EVENT_MASK);
		super.getFrame().setContentPane(assemble());
		this.amIP1 = !codeBreaker;
		this.executor = Executors.newCachedThreadPool();
		this.updateScores(0, 0);
		this.initialize(codeBreaker);
	}
	
	
	private void initialize(boolean codeBreaker)
	{
		this.codeBreaker = codeBreaker;
		if(!this.codeBreaker) 
		{
			this.toggle(super.getCodebtn());
			this.send.setEnabled(true);
			super.getTextArea().setText("Insert the hidden code\n");
		}
			
	}
	public void setPlayer(HumanGUIPlayer p )
	{
		if(this.player==null)
		{
			this.player = p;
			this.showGUI();
		}
		else throw new RuntimeException("Player already set!");
			
	}
	
	private void toggle(JButton[] toEnable)
	{
		for(JButton b : toEnable)
		{
			b.setEnabled(!b.isEnabled());
			if(b.getBorder()==highlight)
				b.setBorder(BorderFactory.createEmptyBorder());
			else
				b.setBorder(highlight);	
		}
	}
	private void makeCursor(String code, char src)
	{
		Cursor c = super.getToolkit().createCustomCursor(src=='c' ? super.getCodep().get(code)  : super.getKeyp().get(code), new Point(1,1), code + (src=='c'? "CodePeg" : "KeyPeg" ));
		super.getFrame().setCursor(c);
	}

	private void mousePressed(AWTEvent e)
	{
		MouseEvent me = (MouseEvent) e;
		if(SwingUtilities.isRightMouseButton(me)) 
		{
			super.getFrame().setCursor(Cursor.getDefaultCursor());
			me.consume();
		}
	}
	
	private void getCode(JButton[] src)
	{
		Code code = null;
		String str = Arrays.stream(src).map(x->x.getActionCommand()+" ").reduce("", String::concat);
		try {
			code = InfoManager.getInfo().parseCodeString(str);
		}catch(Exception ex){
			super.getTextArea().append(ex.getMessage()+"\n");
		}
		if(code!=null) 
		{
			this.toggle(src);
			this.send.setEnabled(false);
			if(InfoManager.getInfo().getStatus() == Status.AWAITINGCODE)
				this.player.receiveCode(code);
			else
				this.player.receiveGuess(code);
		}
	}
	private void getResponse(JButton[] src)
	{
		Outcome response = null;
		String str = Arrays.stream(src).map(x->x.getActionCommand()+" ").reduce("", String::concat);
		try {
			response = InfoManager.getInfo().parseOutcomeString(str);
		}catch(Exception ex){
			super.getTextArea().append(ex.getMessage()+"\n");
		}
		if(response!=null)
		{
			this.toggle(src);
			this.send.setEnabled(false);
			this.player.receiveOutcome(response);
		}
	}
	
	private void sendHandler(ActionEvent e)
	{
		switch(InfoManager.getInfo().getStatus())
		{
			case AWAITINGCODE: this.executor.execute(()->getCode(super.getCodebtn()));  break;
			case AWAITINGGUESS: this.executor.execute(()->getCode(super.getPegbtn()[InfoManager.getInfo().getTurn()])); break;
			case AWAITINGRESPONSE: this.executor.execute(()->getResponse(super.getKeybtn()[InfoManager.getInfo().getTurn()]));  break;
		}

	}
	private void btnMainHandler(ActionEvent e)
	{
		 JButton tmp = (JButton) e.getSource();
		 String actionCommand = tmp.getActionCommand();
		 if(actionCommand.equals("EMPTY") && super.getFrame().getCursor().getType() == Cursor.CUSTOM_CURSOR && super.getFrame().getCursor().getName().contains("CodePeg"))
		 {
			 tmp.setIcon(new ImageIcon(super.getCodep().get(super.getFrame().getCursor().getName().replace("CodePeg", "")))); //NULL?
			 tmp.setDisabledIcon(tmp.getIcon());
			 tmp.setActionCommand(super.getFrame().getCursor().getName().replace("CodePeg", ""));
		 }
		 else
		 {
			 tmp.setIcon(null);
			 tmp.setActionCommand("EMPTY");
		 }
	}
	
	private void btnKeyHandler(ActionEvent e)
	{
		JButton tmp = (JButton) e.getSource();
		 String actionCommand = tmp.getActionCommand();
		 if(actionCommand.equals("EMPTYKEYSLOT") && super.getFrame().getCursor().getType() == Cursor.CUSTOM_CURSOR && super.getFrame().getCursor().getName().contains("KeyPeg"))
		 {
			 tmp.setIcon(new ImageIcon(super.getKeyp().get(super.getFrame().getCursor().getName().replace("KeyPeg", ""))));
			 tmp.setDisabledIcon(tmp.getIcon());
			 tmp.setActionCommand(super.getFrame().getCursor().getName().replace("KeyPeg", ""));
		 }
		 else
		 {
			 tmp.setIcon(new ImageIcon(super.getStruct().get("SNKEY")));
			 tmp.setDisabledIcon(tmp.getIcon());
			 tmp.setActionCommand("EMPTYKEYSLOT");
		 }
	}
	
	private void btnSideHandler(ActionEvent e)
	{
		String actionCommand = ((JButton)e.getSource()).getActionCommand();
		this.makeCursor(actionCommand,'c');	
	}
	
	private void btnLowSideHandler(ActionEvent e)
	{
		String actionCommand = ((JButton)e.getSource()).getActionCommand();
		this.makeCursor(actionCommand,'k');	
	}
	

	
	private JPanel makeSidePanelPegPool()
	{
		//side panel code pegs
		JPanel side = new JPanel();
		side.setLayout(new GridLayout(this.pegs,1));		
		for(Map.Entry<String,BufferedImage> img : super.getCodep().entrySet())
		{
			JLabel label = new JLabel();
			label.setLayout(new GridLayout());
			label.setIcon(new ImageIcon(super.getStruct().get("EMPTYFRAME")));
			JButton tmp = createButton(new ImageIcon(img.getValue()),true);			
			tmp.addActionListener(this::btnSideHandler);
			tmp.setActionCommand(img.getKey());
			label.add(tmp);
			side.add(label);
		}
		return side;
	}
	
	private JPanel makeSidePanelKeyPool()
	{
		//side panel key pegs
		JPanel lowside = new JPanel();
		lowside.setLayout(new GridLayout());	
		JLabel label = new JLabel();
		label.setLayout(new GridLayout(2,1));
		label.setIcon(new ImageIcon(super.getStruct().get("EMPTYFRAME")));
		for(Map.Entry<String,BufferedImage> img : super.getKeyp().entrySet())
		{
			JButton tmp = createButton(new ImageIcon(img.getValue()),true);			
			tmp.addActionListener(this::btnLowSideHandler);
			tmp.setActionCommand(img.getKey());
			label.add(tmp);
		}
		lowside.add(label);
		return lowside;
	}
	private JPanel assemble()
	{	
		JPanel full = (JPanel) super.getFrame().getContentPane();
		Arrays.stream(super.getPegbtn()).parallel().flatMap(Arrays::stream).forEach(x->x.addActionListener(this::btnMainHandler));
		Arrays.stream(super.getKeybtn()).parallel().flatMap(Arrays::stream).forEach(x->x.addActionListener(this::btnKeyHandler));
		Arrays.stream(super.getCodebtn()).forEach(x->x.addActionListener(this::btnMainHandler));
	
		//send button
		this.send = new JButton();
		this.send.setText("Send");
		this.send.setEnabled(false);
		this.send.addActionListener(this::sendHandler);
		
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 1;
		c.gridy = 0;
		full.add(this.makeSidePanelPegPool(), c);
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 1;
		c.gridy = 1;
		full.add(this.makeSidePanelKeyPool(), c);
		c.anchor = GridBagConstraints.PAGE_END;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;      
		c.gridwidth = 2;   
		c.gridy = 2;       
		full.add(send,c);
		
		return full;
	}

	
	@Override
	public void update(Observable o, Object arg)
	{
		if(this.codeBreaker && arg instanceof Outcome || !this.codeBreaker && arg instanceof Code)
			super.update(o, arg);
			
	}
	@Override
	public void reset()
	{
		super.reset();
		this.initialize(!codeBreaker);
	}
	
	public void nextTurn()
	{
		this.send.setEnabled(true);
		if(this.codeBreaker) 
		{
			this.toggle(super.getPegbtn()[InfoManager.getInfo().getTurn()]);		
			super.getTextArea().setText("Insert your guess\n");
		}			
		else 
		{
			this.toggle(super.getKeybtn()[InfoManager.getInfo().getTurn()]);
			super.getTextArea().setText("Insert the response\n");
		}
			
	}
	
	private void updateScores(int p1, int p2)
	{
		super.getP1score().setText("Player 1 "+(amIP1?"(You)":"")+" score: "+p1+" ");
		super.getP2score().setText("Player 2 "+(amIP1?"":"(You)")+" score: "+p2+" ");
	}
	
	public void showGUI()
	{
		if(this.player==null) throw new RuntimeException("gui has no player associated!");
		super.showGUI();
	}
}
