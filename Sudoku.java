import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Sudoku {
	public static void main(String[] args) {
		setup();
	}
		
	public static void setup()
	{
		int n = 9;   //You can change n to change the grid layout of the board, that is, the game board of the puzzle
		int[][] pseudoboard = new int[n][n];
		int[][] boardsoln = new int[n][n];
		boolean built = false;
		while(!built)
			built = buildsudoku(n,pseudoboard,boardsoln);
		
		JFrame frame = new JFrame();
		frame.setBounds(450, 100, 580, 570);
		frame.setTitle("Sudoku Game");
		frame.setResizable(false);
		JPanel panel = new JPanel(new BorderLayout());
		
		JPanel board = new JPanel();
		board.setPreferredSize(new Dimension(500,500));
		board.setLayout(new GridLayout(n,n));
		JTextField[][] fieldref = new JTextField[n][n];
		for(int i=0;i<n;i++)
			for(int j=0;j<n;j++)
			{
				JTextField field = new JTextField();
				field.setHorizontalAlignment(JTextField.CENTER);
				if(pseudoboard[i][j]!=0)
				{
					field.setText(Integer.toString(pseudoboard[i][j]));
					field.setEditable(false);
				}
				if(((i+1)%(int)Math.sqrt(n))==0 && ((j+1)%(int)Math.sqrt(n))==0)
					field.setBorder(BorderFactory.createMatteBorder(1, 1, 3, 3, Color.BLACK));
				else if(((i+1)%(int)Math.sqrt(n))==0)
					field.setBorder(BorderFactory.createMatteBorder(1, 1, 3, 1, Color.BLACK));
				else if(((j+1)%(int)Math.sqrt(n))==0)
					field.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 3, Color.BLACK));
				else
					field.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
				board.add(field);
				fieldref[i][j] = field;
			}
		JPanel control = new JPanel(new FlowLayout());
		JButton reset = new JButton("Reset");
		JButton submit = new JButton("Submit");
		JButton solve = new JButton("Solve");
		JButton newgame = new JButton("New Game");
		control.add(reset);
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				for(int i=0;i<n;i++)
					for(int j=0;j<n;j++)
						if(pseudoboard[i][j]==0)
							fieldref[i][j].setText("");
			}
		});
		control.add(submit);
		submit.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int[][] submission = new int[n][n];
				boolean allinp = true;
				for(int i=0;i<n;i++)
				{	
					for(int j=0;j<n;j++)
						if(fieldref[i][j].getText().isEmpty())
						{
							allinp = false;
							break;
						}
					if(!allinp)
						break;
				}
				if(!allinp)
					JOptionPane.showMessageDialog(new JFrame(), "Fill all the boxes to submit.");
				else
				{
					boolean rightinp = true;
					for(int i=0;i<n;i++)
					{
						for(int j=0;j<n;j++)
						{
							try{
								submission[i][j] = Integer.parseInt(fieldref[i][j].getText());
								if(submission[i][j]<=0 || submission[i][j]>n)
								{
									rightinp = false;
									break;
								}
							}
							catch(Exception exp){
								rightinp = false;
								break;
							}
						}
						if(!rightinp)
							break;	
					}
					if(!rightinp)
						JOptionPane.showMessageDialog(new JFrame(), "Wrong Input");
					else
					{
						boolean correct = true;
						for(int i=0;i<n;i++)
						{
							for(int j=0;j<n;j++)
							{
								correct = issafe(n,submission,i,j);
								if(!correct)
									break;
							}
							if(!correct)
								break;
						}
						if(!correct)
							JOptionPane.showMessageDialog(new JFrame(), "Wrong Answer. Try again.");
						else {
							int a = JOptionPane.showConfirmDialog(new JFrame(), "Right Answer. You Won!! Do you want to play another game?");
							if(a == JOptionPane.YES_OPTION) {
								frame.dispose();
								setup();
							}
							else
								frame.dispose();
						}
					}
				}
			}
		});
		control.add(solve);
		solve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				for(int i=0;i<n;i++)
					for(int j=0;j<n;j++)
						fieldref[i][j].setText(Integer.toString(boardsoln[i][j]));
			}
		});
		control.add(newgame);
		newgame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				frame.dispose();
				setup();
			}
		});
		
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.add(board, BorderLayout.CENTER);

		control.setPreferredSize(new Dimension(500, 50));  
		wrapper.add(control, BorderLayout.SOUTH);

		panel.add(wrapper, BorderLayout.CENTER);

		frame.add(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static boolean buildsudoku(int n, int[][] pseudoboard, int[][] boardsoln)
	{
		Random random = new Random();
		int clues = 34;   //You can change the number of clues to adjust the difficulty level of the game
		int p = 0;
		for(int i=0;i<n;i++)
			for(int j=0;j<n;j++)
				pseudoboard[i][j] = 0;
		while(p<=clues)
		{
			int ri = random.nextInt(1000000000)%n;
			int ci = random.nextInt(1000000000)%n;
			int val = random.nextInt(1000000000)%(n+1);
			if(val==0 || pseudoboard[ri][ci]!=0)
				continue;
			pseudoboard[ri][ci] = val;
			boolean safe = issafe(n,pseudoboard,ri,ci);
			if(!safe)
			{
				pseudoboard[ri][ci] = 0;
				continue;
			}
			p++;
		}
		for(int i=0;i<n;i++)
			for(int j=0;j<n;j++)
				boardsoln[i][j] = pseudoboard[i][j];
		boolean solnexist = fillsudoku(n,boardsoln,0,0);
		if(solnexist)
			return true;
		else
			return false;
	}
	
	public static boolean fillsudoku(int n, int[][] boardsoln, int ri, int ci)
	{
	    if(ci==n)
	    {
	        ri = ri+1;
	        ci = 0;
	    }
	    if(ri==n && ci==0)
	        return true;
	    if(boardsoln[ri][ci]!=0)
	    {
	        boolean retval = fillsudoku(n,boardsoln,ri,ci+1);
	        return retval;
	    }
	    for(int i=1;i<=n;i++)
	    {
	        boardsoln[ri][ci] = i;
	        boolean safe = issafe(n,boardsoln,ri,ci);
	        if(!safe)
	        {
	            boardsoln[ri][ci] = 0;
	            continue;
	        }
	        boolean retval = fillsudoku(n,boardsoln,ri,ci+1);
	        if(!retval)
	        {
	        	boardsoln[ri][ci] = 0;
	        	continue;
	        }
	        else
	        	return true;
	    }
	    return false;
	}
	
	public static boolean issafe(int n, int[][] pseudoboard, int ri, int ci)
	{
	    boolean[] check = new boolean[n];
	    for(int i=0;i<n;i++)
	        check[i] = false;
	    for(int i=0;i<n;i++)
	    {
	        if(pseudoboard[ri][i]==0)
	            continue;
	        if(!check[pseudoboard[ri][i]-1])
	            check[pseudoboard[ri][i]-1] = true;
	        else
	            return false;
	    }
	    for(int i=0;i<n;i++)
	        check[i] = false;
	    for(int i=0;i<n;i++)
	    {
	        if(pseudoboard[i][ci]==0)
	            continue;
	        if(!check[pseudoboard[i][ci]-1])
	            check[pseudoboard[i][ci]-1] = true;
	        else
	            return false;
	    }
	    int sn = (int)Math.sqrt(n);
	    int boxri = ri/sn;
	    int boxci = ci/sn;
	    for(int i=0;i<n;i++)
	        check[i] = false;
	    for(int i=0;i<sn;i++)
	        for(int j=0;j<sn;j++)
	        {
	            if(pseudoboard[boxri*sn+i][boxci*sn+j]==0)
	                continue;
	            if(!check[pseudoboard[boxri*sn+i][boxci*sn+j]-1])
	                check[pseudoboard[boxri*sn+i][boxci*sn+j]-1] = true;
	            else
	                return false;
	        }
	    return true;
	}
}
