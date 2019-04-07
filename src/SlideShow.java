/*
 * CS-499 Capstone Final Project 
 * Author: Jason Luu
 * 
 * All program source files should start with:
 * Name of the program: SNHU Travel
 * Author: Unknown 
 * Acknowledgement: Source code from SNHU CS-250 Software Development Lifecycle
 * Date of the update: 4-6-2019
 * 
 * The original code is to display the top 5 travel destinations. The 5 jpeg files must be existed 
 * in the resources folder in order for the code executes correctly.   
 *
 * This program was modified to meet the requirement of CS-499 Capstone class
 * Features added were for user to have an option of creating a new userID/Password or login, 
 * authenticate user password through MD5 hashing, and store data. 
 *  
 *  
 *  Note: Using the follow user name and password for testing
 *  
 *  		User name		password
 *  		-------------------------        
 *          |Alpha			|One    |
 *          -------------------------
 *          |Beta			|Two	|
 *          -------------------------
 *          |Charlie 		|SON	|
 *          -------------------------
 *          |Delta 			|Java	|
 *          -------------------------
 * 
 */

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;

public class SlideShow extends JFrame {

	/**
	 * Local variables
	 */
	private static final long serialVersionUID = 1L;
	//Declare Variables
	private JPanel slidePane;
	private JPanel textPane;
	private JPanel buttonPane;
	private CardLayout card;
	private CardLayout cardText;
	private JButton btnPrev;
	private JButton btnNext;
	private JLabel lblSlide;
	private JLabel lblTextArea;
	private SlideShowDatabase slideShowDatabase;
	private String my_username;

	/**
	 * Create the application.
	 */
	public SlideShow() throws HeadlessException {
		slideShowDatabase=new RAM_Database();	//instantiate database class
		my_username=login();					//attempt to login, returns username String
		initComponent(5,470,790,50);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initComponent(int x,int y,int w,int h) {
		//Initialize variables to empty objects
		card = new CardLayout();
		cardText = new CardLayout();
		slidePane = new JPanel();
		textPane = new JPanel();
		textPane.setBackground(Color.CYAN);
		textPane.setBounds(x,y,w,h);
		textPane.setVisible(true);
		buttonPane = new JPanel();
		btnPrev = new JButton();
		btnNext = new JButton();
		lblSlide = new JLabel();
		lblTextArea = new JLabel();

		//Setup frame attributes
		setSize(800, 600);
		setLocationRelativeTo(null);
		setTitle("Top 5 Destinations SlideShow");
		getContentPane().setLayout(new BorderLayout(10, 50));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Setting the layouts for the panels
		slidePane.setLayout(card);
		textPane.setLayout(cardText);
		
		//logic to add each of the slides and text
		for (int i = 1; i <= 5; i++) {
			lblSlide = new JLabel();
			lblTextArea = new JLabel();
			lblSlide.setText(getResizeIcon(i));
			lblTextArea.setText(getTextDescription(i));
			slidePane.add(lblSlide, "card" + i);
			textPane.add(lblTextArea, "cardText" + i);
		}

		getContentPane().add(slidePane, BorderLayout.CENTER);
		getContentPane().add(textPane, BorderLayout.SOUTH);

		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

		btnPrev.setText("Previous");
		btnPrev.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				goPrevious();
			}
		});
		buttonPane.add(btnPrev);

		btnNext.setText("Next");
		btnNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				goNext();
			}
		});
		buttonPane.add(btnNext);

		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	}

	/**
	 * Previous Button Functionality
	 */
	private void goPrevious() {
		card.previous(slidePane);
		cardText.previous(textPane);
	}
	
	/**
	 * Next Button Functionality
	 */
	private void goNext() {
		card.next(slidePane);
		cardText.next(textPane);
	}
	
	/**
	 * Get the username of the account currently logged in
	 */
	public String getLoginUsername()
	{
		return my_username;
	}
	
	/**
	 * returns true if account successfully completed the login process
	 */
	public boolean isLoggedIn()
	{
		return getLoginUsername()!=null && getLoginUsername().length()>0;
	}
	
	//prompt user for a username, but do not check of result
	//Ref: https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
	private static String getUserName()
	{
		String title="Enter Username";
		JFrame frame = new JFrame(title);
		String s = (String)JOptionPane.showInputDialog(
                frame,
                "Enter your username:",
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,//dialog box icon
                null,//list of options, null for text entry
                "");//pre-populated text
		return s;
	}
	
	//prompt user for password
	//if this is a password for a new account (is_new_account), then alter prompt text slightly to reflect this password is being used to create a new account
	//if the previous password entered was incorrect (is_reenter_password), add a status flag to the next pop-up indicating the previous attempt failed
	
	private static String getPassword(String username,boolean is_new_account,boolean is_reenter_password)
	{
		String prompt;
		if(is_new_account) prompt="To create a new account '"+username+"', enter password:";
		else prompt="Enter password for account '"+username+"':";
		if(is_reenter_password) prompt="Incorrect password entered, try again:\n"+prompt;
		
		String title="Enter "+(is_new_account?"New ":"")+"Account Password";
		JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String s = (String)JOptionPane.showInputDialog(
                frame,
                prompt,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,		//dialog box icon
                null,		//list of options, null for text entry
                "");		//pre-populated text
		return s;
	}
	
	private static void notifyNewAccountCreated(String username)
	{
		String title="New Account Created";
		JFrame frame = new JFrame(title);
		JOptionPane.showMessageDialog(frame,"New account created with username: "+username,title,JOptionPane.INFORMATION_MESSAGE);
	}
	
	private static void notifyUserCrash()
	{
		String title="Invalid Data Entry";
		JFrame frame = new JFrame(title);
		JOptionPane.showMessageDialog(frame,"User performed an illegal action, exiting...",title,JOptionPane.ERROR_MESSAGE);
	}
	
	//prompt user for username
	//if username does not exist in database, prompt user for new password, create new account in database, notify user account was created
	//prompt user for password
	private String login()
	{
		String username=getUserName();//prompt for username, return String
		//but user may enter invalid data, closer prompt, etc, so handle exceptions by displaying a pop-up and then exiting (return null)
		
		if(username==null || username.length()<=0)
		{
			notifyUserCrash();//FUTURE: implement crash handling, re-prompt for username, etc
			return null;
		}
		if(!slideShowDatabase.isAccountExists(username))
		{
			//prompt for new password, notify user account has been created
			String new_password=getPassword(username,true,false);//prompt for new password to associate with new account, return String
			if(new_password==null || new_password.length()<=0)
			{
				notifyUserCrash();
				return null;
			}
			slideShowDatabase.createNewAccount(username,new_password);
			notifyNewAccountCreated(username);
		}
		boolean is_reenter_password=false;//flag to track when to prompt user to enter password again
		do{
			String existing_password=getPassword(username,false,is_reenter_password);
			if(existing_password==null || existing_password.length()<=0)
			{
				notifyUserCrash();
				return null;
			}
			is_reenter_password=!slideShowDatabase.isUsernamePasswordMatch(username,existing_password);
		}while(is_reenter_password);//continue to prompt user for password until they get it right or give up
		return username;
	}

	/**
	 * Method to get the images
	 */
	private String getResizeIcon(int i) {
		String image = "";
		String[] image_list= new String[] {"/resources/GrandCanyon.jpg",
				   						   "/resources/BurjKhalifa.jpg",
										   "/resources/SanFrancisco.jpg",
										   "/resources/SydneyOperaHouse.jpg",
										   "/resources/Tokyo.jpg"};
		if(i<=0 || i>image_list.length) throw new IllegalArgumentException("Illegal getResizeIcon index: "+i);
		image="<html><body><img width= '800' height='500' src='" + getClass().getResource(image_list[i-1]) + "'</body></html>";
		
		return image;
	}
	
	/**
	 * Method to get the text values
	 */
	private String getTextDescription(int i) {
		String text = ""; 
		int MAX_IMAGE_COUNT=5;
		if(i<=0 || i>MAX_IMAGE_COUNT) throw new IllegalArgumentException("Illegal getTextDescription index: "+i);
		if(i==1) text = "<html><body><font size='5'>#1 The Grand Canyon.</font> <br>Spectacular canyon views and hiking.</body></html>";
		else text="<html><body>#"+i+" Top Destination</body></html>";
		return text;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				SlideShow ss = new SlideShow();
				if(ss.isLoggedIn())//only display slideshow if login was successful
					ss.setVisible(true);
			}
		});
	}
}

//Abstract class to define universally used database operations
abstract class SlideShowDatabase{
	
	//custom method to encrypt password data
	//Ref: https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
	protected String encode(String passwordToHash)
	{
		passwordToHash+=passwordToHash+"salt";
		//salt then encrypt
        // Create MessageDigest instance for MD5
		try {
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        //Add password bytes to digest
	        md.update(passwordToHash.getBytes());
	        //Get the hash's bytes
	        byte[] bytes = md.digest();
	        //This bytes[] has bytes in decimal format;
	        //Convert it to hexadecimal format
	        StringBuilder sb = new StringBuilder();
	        for(int i=0; i< bytes.length ;i++)
	        {
	            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        //Get complete hashed password in hex format
	        String generatedPassword = sb.toString();
	        return generatedPassword;
		}catch(NoSuchAlgorithmException nsae) {}//silence error
		return null;
	}
	
	public abstract boolean isAccountExists(String username);
	public abstract boolean createNewAccount(String username,String password);
	public abstract boolean isUsernamePasswordMatch(String username,String password);
	//FUTURE: Delete and Update from CRUD
}

class DumbDatabase extends SlideShowDatabase{
	public boolean isAccountExists(String username)
	{
		return false;
	}

	public boolean createNewAccount(String username,String password)
	{
		return true;
	}

	public boolean isUsernamePasswordMatch(String username,String password)
	{
		return true;
	}
}

class RAM_Database extends SlideShowDatabase{

	//database holding username-password pairs as two element string lists: {username,password}
	private ArrayList<String[]> database_username_password;
	
	public RAM_Database()
	{
		//instantiate empty database
		database_username_password= new ArrayList<String[]>();
		//pre-populating database with dummy values
		database_username_password.add(new String[]{"a",encode("a")});
		database_username_password.add(new String[]{"Alpha",encode("One")});
		database_username_password.add(new String[]{"Beta",encode("Two")});
		database_username_password.add(new String[]{"Charlie",encode("JSON")});
		database_username_password.add(new String[]{"Delta",encode("Java")});
	}
	
	//Read from CRUD
	//pre-con: username is not null
	//look at database, determine if given username already exists
	public boolean isAccountExists(String username)
	{
		for(int iter=0;iter<database_username_password.size();iter++)
		{
			String[] username_password_pair=database_username_password.get(iter);
			String database_username=username_password_pair[0];
			if(database_username.equals(username)) return true;
		}
		return false;
	}
	
	//Create from CRUD
	//precon: account does not already exist in database
	//returns is_success
	public boolean createNewAccount(String username,String password)
	{
		System.out.println("createNewAccount: length: "+database_username_password.size());
		database_username_password.add(new String[] {username,encode(password)});
		return true;
	}

	//Read from CRUD
	//look for username and salted password in database, return true if found
	public boolean isUsernamePasswordMatch(String username,String password)
	{
		String password_encrypted=encode(password);
		for(int iter=0;iter<database_username_password.size();iter++)
		{
			String[] username_password_pair=database_username_password.get(iter);
			String database_username=username_password_pair[0];
			String database_password=username_password_pair[1];
			//if(database_username==username && database_password==password_encrypted) return true;
			if(database_username.equals(username) && database_password.equals(password_encrypted)) return true;
		}
		return false;
	}
}