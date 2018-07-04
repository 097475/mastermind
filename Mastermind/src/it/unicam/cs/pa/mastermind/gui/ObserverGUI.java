package gui;




import java.awt.AWTEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import core.Code;
import core.CodePeg;
import core.Match;
import core.Outcome;
import core.ResourceLoader;
import core.Settings;

@SuppressWarnings("deprecation")
public class ObserverGUI implements Observer{ 
	private int codeSize;
	private int boardSize;
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	private final Properties settings;
	private JFrame frame = new JFrame("Mastermind");
	private Map<String,BufferedImage> codep,keyp,struct;
	private JButton[][] pegbtn;
	private JButton[] codebtn;
	private JButton[][] keybtn;
	private JTextArea textArea;
	private JLabel p1score,p2score;

	public ObserverGUI(Map<String,CodePeg> pegSet)
	{
		this.settings = Settings.getGameProperties();
		this.codeSize = Integer.parseInt(settings.getProperty("CodeSize"));
		this.boardSize = Integer.parseInt(settings.getProperty("BoardSize"));
		this.codep = ResourceLoader.getCodeResources(pegSet);
		this.keyp = ResourceLoader.getKeyResources();
		this.struct = ResourceLoader.getStructResources();
		this.frame.setContentPane(assemble());
	}


	protected JButton createButton(ImageIcon icon, boolean enabled)
	{
		JButton tmp = new JButton(icon);
		tmp.setContentAreaFilled(false);
		tmp.setBorder(BorderFactory.createEmptyBorder());
		tmp.setEnabled(enabled);
		tmp.setDisabledIcon(icon);
		return tmp;
	}

	private JPanel leftSide()
	{
		this.pegbtn = new JButton[this.boardSize][this.codeSize];
		this.keybtn = new JButton[this.boardSize][this.codeSize];
		//guessrows
		JPanel main = new JPanel();
		main.setLayout(new GridLayout(this.boardSize,this.codeSize+1));
		for(int i = 0; i<this.boardSize; i++)
		{
			JPanel row = new JPanel();
			row.setLayout(new GridLayout(1,this.codeSize+1));
			for(int j = 0; j<this.codeSize; j++)
			{
				JLabel label = new JLabel(new ImageIcon(this.struct.get("EMPTYSN")));
				label.setLayout(new GridLayout());
				this.pegbtn[i][j] = createButton(null,false);
				this.pegbtn[i][j].setActionCommand("EMPTY");
				label.add(this.pegbtn[i][j]);
				row.add(label);
			}

			//guess row keys
			for(int k = 0; k <(this.codeSize/4)+1; k++)
			{
				JLabel label = new JLabel(new ImageIcon(struct.get("EMPTYFRAME")));
				label.setLayout(new GridLayout(2,2));
				for(int j = 0; j < 4 && j+(4*k) < this.codeSize; j++)
				{
					this.keybtn[i][j+(4*k)] = createButton(new ImageIcon(this.struct.get("SNKEY")),false);
					this.keybtn[i][j+(4*k)].setActionCommand("EMPTYKEYSLOT");
					label.add(this.keybtn[i][j+(4*k)]);
				}
				if(!(k*4 == this.codeSize))
					row.add(label);
			}

			main.add(row);
		}
		return main;
	}

	private JPanel codeRow()
	{
		this.codebtn = new JButton[this.codeSize];
		//secret code
		JPanel code = new JPanel();
		code.setLayout(new GridLayout(1,this.codeSize));
		for(int i = 0; i < this.codeSize ; i++)
		{
			JLabel label = new JLabel(new ImageIcon(this.struct.get("EMPTYSN")));
			label.setLayout(new GridLayout());
			this.codebtn[i] = createButton(null,false);
			this.codebtn[i].setActionCommand("EMPTY");
			label.add(this.codebtn[i]);
			code.add(label);
		}
		return code;
	}

	private JPanel rightSide()
	{
		//console
		this.textArea = new JTextArea(20,40);
		this.textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea); 
		scrollPane.setPreferredSize(textArea.getPreferredScrollableViewportSize());

		//scores
		JPanel scorepanel = new JPanel();
		scorepanel.setLayout(new BoxLayout(scorepanel,BoxLayout.X_AXIS));
		this.p1score = new JLabel("Player 1 score: 0 ");
		this.p2score = new JLabel("Player 2 score: 0 ");
		scorepanel.add(this.p1score);
		scorepanel.add(Box.createHorizontalStrut(20));
		scorepanel.add(this.p2score);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
		rightPanel.add(scorepanel);
		rightPanel.add(Box.createVerticalStrut(10));
		rightPanel.add(scrollPane);

		return rightPanel;
	}
	private JPanel assemble()
	{		
		JPanel full = new JPanel(new GridBagLayout());	
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		full.add(this.leftSide(), c);
		c.anchor = GridBagConstraints.PAGE_END;
		c.gridx = 0;
		c.gridy = 1;
		full.add(this.codeRow(), c);
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.insets = new Insets(20,20,20,20);
		c.gridx = 2;
		c.gridy = 0;
		full.add(this.rightSide(), c);
		return full;
	}


	@Override
	public void update(Observable o, Object arg) {
		if(arg instanceof Code)  
			this.updateGuess((Code) arg);
		else if(arg instanceof Outcome)
			this.updateOutcome((Outcome) arg);
		else throw new RuntimeException("Unknown Object!");
	}

	private void updateGuess(Code guess)
	{
		int row = Match.getMatch().getTurn();
		int j = 0;
		for(CodePeg p : guess.get())
		{
			if(p!=CodePeg.EmptyEnum.EMPTY)
			{
				this.pegbtn[row][j].setDisabledIcon(new ImageIcon(codep.get(p.toString())));
				this.pegbtn[row][j].setIcon(new ImageIcon(codep.get(p.toString())));
			}
			j++;
		}	
	}

	private void updateOutcome(Outcome o)
	{
		int row = Match.getMatch().getTurn();
		int j=0;
		for(int i = 0; i <o.getBlackPegs();i++)
		{
			this.keybtn[row][j].setDisabledIcon(new ImageIcon(keyp.get("BLACK")));
			this.keybtn[row][j].setIcon(new ImageIcon(keyp.get("BLACK")));
			j++;
		}

		for(int i = 0; i <o.getWhitePegs();i++)
		{
			this.keybtn[row][j].setDisabledIcon(new ImageIcon(keyp.get("WHITE")));
			this.keybtn[row][j].setIcon(new ImageIcon(keyp.get("WHITE")));
			j++;
		}

	}

	public void setCode(Code c)  //inherit?
	{
		int i = 0;
		for(CodePeg p : c.get())
		{
			if(p!=CodePeg.EmptyEnum.EMPTY)
			{
				this.codebtn[i].setDisabledIcon(new ImageIcon(codep.get(p.toString())));
				this.codebtn[i].setIcon(new ImageIcon(codep.get(p.toString())));
				i++;
			}

		}

	}

	public void reset()
	{
		Arrays.stream(pegbtn).parallel().flatMap(Arrays::stream).forEach(x->x.setIcon(null));
		Arrays.stream(keybtn).parallel().flatMap(Arrays::stream).forEach(x->{x.setIcon(new ImageIcon(struct.get("SNKEY"))); x.setDisabledIcon(x.getIcon());});
		Arrays.stream(codebtn).forEach(x->x.setIcon(null));
	}

	private void updateScores(int p1, int p2)
	{
		this.p1score.setText("Player 1 score: "+p1+" ");
		this.p2score.setText("Player 2 score: "+p2+" ");
	}

	private synchronized void keyPressed(AWTEvent e) {
		if(e.getID() == KeyEvent.KEY_PRESSED)
		{
			this.toolkit.removeAWTEventListener(this::keyPressed);
			this.notify();
		}
	}
	public synchronized void endRound()
	{
		this.updateScores(Match.getMatch().getP1Score(),Match.getMatch().getP2Score());
		this.textArea.setText("Round ended "+Match.getMatch().getRound()+". Press any key to continue...");
		this.toolkit.addAWTEventListener(this::keyPressed, AWTEvent.KEY_EVENT_MASK);
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.reset();
	}

	public void endMatch()
	{
		this.frame.dispose();
	}



	public Toolkit getToolkit() {
		return toolkit;
	}



	public JFrame getFrame() {
		return frame;
	}


	public Map<String, BufferedImage> getCodep() {
		return codep;
	}


	public Map<String, BufferedImage> getKeyp() {
		return keyp;
	}


	public Map<String, BufferedImage> getStruct() {
		return struct;
	}
	
	public JButton[] getCodebtn() {
		return codebtn;
	}

	public JButton[][] getPegbtn() {
		return pegbtn;
	}


	public JButton[][] getKeybtn() {
		return keybtn;
	}


	public JTextArea getTextArea() {
		return textArea;
	}


	public JLabel getP1score() {
		return p1score;
	}


	public JLabel getP2score() {
		return p2score;
	}


	public void showGUI()
	{
		this.getFrame().pack();
		this.getFrame().setVisible(true);
	}

}

