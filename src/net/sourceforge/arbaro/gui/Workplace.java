//  #**************************************************************************
//  #
//  #    Copyright (C) 2003-2006  Wolfram Diestel
//  #
//  #    This program is free software; you can redistribute it and/or modify
//  #    it under the terms of the GNU General Public License as published by
//  #    the Free Software Foundation; either version 2 of the License, or
//  #    (at your option) any later version.
//  #
//  #    This program is distributed in the hope that it will be useful,
//  #    but WITHOUT ANY WARRANTY; without even the implied warranty of
//  #    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  #    GNU General Public License for more details.
//  #
//  #    You should have received a copy of the GNU General Public License
//  #    along with this program; if not, write to the Free Software
//  #    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//  #
//  #    Send comments and bug fixes to diestel@steloj.de
//  #
//  #**************************************************************************/

package net.sourceforge.arbaro.gui;

import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.border.TitledBorder;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JSplitPane;
import javax.swing.JSlider;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.arbaro.params.ParamManager;
import net.sourceforge.arbaro.params.ParamException;
import net.sourceforge.arbaro.export.ExporterFactory;
import net.sourceforge.arbaro.feedback.Console;

/**
 * The main window of Arbaro GUI
 * 
 * @author wdiestel
 */
 public final class Workplace {
	/*
	 * TODO:
	 *  - toolbar buttons for level changes of preview, toggling of leaf showing
	 *  - zoom function for previews
	 *  - show long parameter descriptions when mouse _moves_ to helpInfo,
	 *    smaller window, smaller font?
	 *  - option for showing the whole tree in a (preview) window?
	 * 
	 *  MORE FEATURES:
	 *  - export to 3DS?
	 *  - more kinds of leafs (palm, sphere)
	 *  - bumps for trunks
	 *  - leafs that aren't flat
	 *  - textures
	 *  
	 */

	JFrame frame;
	Config config;

	//Tree tree;
	ParamManager params;
//	ExporterFactory exporterFactory;
	int seed = 13;
	
	PreviewTree previewTree;
	TreePreview frontView;
	TreePreview topView;
	JSlider rotator;
	
	JLabel imageLabel;
	JToolBar toolBar;
	
	JFileChooser fileChooser;
    File treefile = null;
	boolean modified;
	
	static final Color bgClr = new Color(242,242,229);
	static final Color Silver = new Color(142,142,129);
	    
	ParamValueTable valueEditor;
	ParamGroupsView groupsView;
	
    // images
    final static ImageIcon shapeIcon = createImageIcon("images/shape.png","Tree shape");
    final static ImageIcon radiusIcon = createImageIcon("images/radius.png","Trunk radius");
    final static ImageIcon leavesIcon = createImageIcon("images/leaves.png","Leaves");
    final static ImageIcon pruneIcon = createImageIcon("images/pruning.png","Pruning/Envelope");
    final static ImageIcon miscIcon = createImageIcon("images/misc.png","Miscellaneous parameters");
    final static ImageIcon lentapIcon = createImageIcon("images/len_tapr.png","Length and taper");
    final static ImageIcon curveIcon = createImageIcon("images/curve.png","Curvature");
    final static ImageIcon splitIcon = createImageIcon("images/splitting.png","Splitting");
    final static ImageIcon substemIcon = createImageIcon("images/substem.png","Branching");
	final static ImageIcon aboutIcon = createImageIcon("images/arbaro64.png","Arbaro");

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     * 
     * @param path 
     * @param description 
     * @return ImageIcon, or null if the path was invalid
     */
    protected static ImageIcon createImageIcon(String path,
    		String description) {
    	java.net.URL imgURL = Workplace.class.getResource(path);
    	if (imgURL != null) {
    		return new ImageIcon(imgURL, description);
    	} else {
    		System.err.println("Couldn't find file: " + path);
    		return null;
    	}
    }

	
	public Workplace () {

		// create tree with paramDB
		params = new ParamManager();
		//try { params.prepare(13); } catch (Exception e) {};

		// create frame
		frame = new JFrame("Arbaro");

		// create preview Tree
		previewTree = new PreviewTree(params,13 /* seed*/);
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (! shouldSave()) return;
				System.exit(0);
			}
		});
		// set Icon
		java.net.URL imgURL = Workplace.class.getResource("images/arbaro32.png");
		if (imgURL != null) {
			Image icon = Toolkit.getDefaultToolkit().getImage(imgURL);
			frame.setIconImage(icon);
		}
		
		// create file chooser for open/save dialogs
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")+"/trees"));
		
		// read config
		config = new Config();
		applyConfig();
		
		// create GUI
		//AbstractParam.loading=true;
		createGUI();
		groupsView.fireStateChanged();
		frame.setVisible(true);
		setModified(false);
		//AbstractParam.loading=false;
	}
	
	public void applyConfig() {
		// TODO instead of exporterFactory a classes exportOptions and
		// renderOptions should be used here
		seed = Integer.parseInt(config.getProperty("tree.seed",""+seed));
		ExporterFactory.setExportFormat(Integer.parseInt(config.getProperty("export.format",""+ExporterFactory.getExportFormat())));
		ExporterFactory.setRenderW(Integer.parseInt(config.getProperty("povray.width",""+ExporterFactory.getRenderW())));
		ExporterFactory.setRenderH(Integer.parseInt(config.getProperty("povray.height",""+ExporterFactory.getRenderH())));
		ExporterFactory.setOutputPath(config.getProperty("export.path",ExporterFactory.getOutputPath()));
	}
	
	void createGUI() {
//		createActions();
		createMenuBar();
		createToolBar();
		
		// main pane
		JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainPane.setResizeWeight(0.25);

		//////////////// left pane for parameter editing 
		//JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		//leftPane.setResizeWeight(0.2);
		JPanel leftPane = new JPanel();
		leftPane.setLayout(new BoxLayout(leftPane,BoxLayout.PAGE_AXIS));
		
		// parameter groups tree view
		groupsView = new ParamGroupsView();
		JScrollPane scrollPane = new JScrollPane(groupsView);
		//scrollPane.setBorder(BorderFactory.createEmptyBorder());
		leftPane.add(scrollPane);//,JSplitPane.TOP);

		// parameter value editor
		valueEditor = new ParamValueTable(params.getParamEditing());
		leftPane.add(valueEditor);//,JSplitPane.BOTTOM);

		valueEditor.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				try {
					setModified(true);
					previewTree.remake(true);
					// valueEditor.noError();

					// FIXME: only necessary, when species
					// param changed
					frame.setTitle("Arbaro ["+params.getParamEditing().getSpecies()+"]");
					
				} catch (ParamException err) {
					// FIXME handle this in ParamManager somewhere?
					Console.errorOutput(err.toString());
					valueEditor.showError(err);
				} catch (Exception err) {
					Console.printException(err);
				}
			}
		});
		groupsView.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				// show group params in the value table
				try {
					int level = groupsView.getGroupLevel();
					String group = groupsView.getGroupName();
					
					// show parameters in value editor
					valueEditor.showGroup(group,level);

					// change preview trees level
					if (level==params.getParamEditing().getGeneralLevel()) {
						if (group.equals("LEAVES") || group.equals("LEAVESADD"))
							previewTree.setShowLevel(params.getParamEditing().getLevels());
						else
							previewTree.setShowLevel(1);
					} else {
						previewTree.setShowLevel(level);
					}
					
					// change explaining image
					ImageIcon icon;
					icon = shapeIcon;
					if (group.equals("SHAPE")) icon = shapeIcon;
					else if (group.equals("TRUNK")) icon=radiusIcon;
			    	else if (group.equals("LEAVES") || group.equals("LEAVESADD")) icon=leavesIcon;
			    	else if (group.equals("PRUNING")) icon=pruneIcon;
			    	else if (group.equals("MISC")) icon=miscIcon;
					else if (group.equals("LENTAPER")) icon=lentapIcon;			    	
			    	else if (group.equals("CURVATURE")) icon=curveIcon;
		    		else if (group.equals("SPLITTING")) icon=splitIcon;
		    		else if (group.equals("BRANCHING")) icon=substemIcon;
		    					    	
					imageLabel.setIcon(icon);
					((TitledBorder)imageLabel.getBorder()).setTitle(icon.getDescription());
					
					previewTree.remake(true);
					
				} catch (Exception err) {
					Console.printException(err);
				}
			}
		});
		
		//FIXME: 
		//leftPanel.setMinimumSize(new Dimension(100,100));
		//leftPane.setBackground(Color.RED);
		
		mainPane.add(leftPane,JSplitPane.LEFT);

		/////////////////// right pane with previews
		JPanel rightPane = new JPanel();
		rightPane.setOpaque(true);
		//rightPane.setBackground(Silver);
		
		//rightPane.setBackground(Color.LIGHT_GRAY);
		GridBagLayout grid = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		rightPane.setLayout(grid);
		
		constraints.insets = new Insets(1,1,0,0);

		// big front view
		JPanel frontViewWS = createFrontView();
		constraints.weightx=1.0;
		constraints.weighty=1.0;
		constraints.fill = GridBagConstraints.BOTH;		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 2;
		grid.setConstraints(frontViewWS,constraints);
		rightPane.add(frontViewWS);

		constraints.weightx=0.2;
		
		// small top view
		topView = new TreePreview(previewTree,TreePreview.PERSPECTIVE_TOP,config);
		topView.setOpaque(true); 
		topView.setBackground(bgClr);
		
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		// constraints.anchor = GridBagConstraints.WEST;
		grid.setConstraints(topView,constraints);
		rightPane.add(topView);

		// other view with explaining images
		imageLabel = new JLabel("", shapeIcon, JLabel.CENTER); 
		imageLabel.setBorder(BorderFactory.createTitledBorder(
    			BorderFactory.createEmptyBorder(2,2,2,2),"Tree shape",
				TitledBorder.CENTER,TitledBorder.BOTTOM));

//		imageLabel.setBorder(BorderFactory.createTitledBorder(
//    			BorderFactory.createLineBorder(Color.RED),"Tree shape",
//				TitledBorder.CENTER,TitledBorder.BOTTOM));
//
		imageLabel.setOpaque(true);
		imageLabel.setBackground(bgClr);
    	
		constraints.weighty=0.2;
		constraints.gridx = 2;
		constraints.gridy = 1;
		// constraints.anchor = GridBagConstraints.WEST;
		//JLabel placeholder = new JLabel("placeholder");
		grid.setConstraints(imageLabel,constraints);
		rightPane.add(imageLabel);
		
		mainPane.add(rightPane,JSplitPane.RIGHT);
		
		// setup main content pane
		mainPane.setPreferredSize(new Dimension(800,600));
		Container contentPane = frame.getContentPane(); 
		contentPane.add(mainPane,BorderLayout.CENTER);

		// add toolbar
		contentPane.add(toolBar,BorderLayout.PAGE_START);
		
		// add status line
//		statusbar = new Statusbar();
//		Font font = statusbar.getFont().deriveFont(Font.PLAIN,12);
//		statusbar.setFont(font);
//		statusbar.addMouseListener(new StatusbarListener());
//		contentPane.add(statusbar,BorderLayout.PAGE_END);
		
		frame.pack();
	}
	
	private JPanel createFrontView() {
		JPanel frontViewWithSlider = new JPanel();
		frontViewWithSlider.setLayout(new BorderLayout());
		frontView = new TreePreview(previewTree,TreePreview.PERSPECTIVE_FRONT,config);
		frontView.setOpaque(true);
		frontView.setBackground(Color.WHITE);
		frontViewWithSlider.add(frontView,BorderLayout.CENTER);
		
		rotator = new JSlider(-180,180);
		rotator.setPaintLabels(true);
		rotator.setPaintTicks(true);
		rotator.setPaintTrack(true);
		rotator.setMinorTickSpacing(10);
		rotator.setMajorTickSpacing(90);
		rotator.setBackground(new Color(250,250,245));
		rotator.setBorder(BorderFactory.createMatteBorder(0,0,0,1,
				frontViewWithSlider.getBackground()));
		
		rotator.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				// fast draw while adjusting rotation slider
				frontView.setDraft(source.getValueIsAdjusting());
				topView.setDraft(source.getValueIsAdjusting());
				// set new rotation
				frontView.setRotation(rotator.getValue());
				topView.setRotation(rotator.getValue());
			}
		});
		frontViewWithSlider.add(rotator,BorderLayout.SOUTH);
		return frontViewWithSlider;
	}

	FileNewAction fileNewAction =  new FileNewAction(); 
	FileOpenAction fileOpenAction = new FileOpenAction(); 
	FileSaveAction fileSaveAction = new FileSaveAction(); 
	ExportTreeAction exportTreeAction = new ExportTreeAction();
	RenderTreeAction renderTreeAction = new RenderTreeAction();
	QuitAction quitAction = new QuitAction();
	
	JToolBar createToolBar() {
		toolBar = new JToolBar();
		
		addToolBarButton(fileNewAction);
		addToolBarButton(fileOpenAction);
		addToolBarButton(fileSaveAction);
		
		toolBar.add(Box.createRigidArea(new Dimension(10,10)));
		
		addToolBarButton(exportTreeAction);
		addToolBarButton(renderTreeAction);
		
//		addToolBarButton("0","Show level 0");
//		addToolBarButton("1","Show level 1");
//		addToolBarButton("2","Show level 2");
//		addToolBarButton("3","Show level 3");
//		addToolBarButton("4..","Show level 4");
//
//		addToolBarButton("L","Show leaves");
		
		return toolBar;
	}
	
	void addToolBarButton(AbstractAction action) {
		
		JButton button = new JButton(action);
		button.setText("");
	    toolBar.add(button);
	}
	
	void createMenuBar() {
		JMenuBar menubar;
		JMenu menu;
		JMenuItem item;
		
		/**** file menu ***/
		
		menubar=new JMenuBar();
		menu = new JMenu("File");
		menu.setMnemonic('F');
		
		// File new
		item = new JMenuItem(fileNewAction);
		menu.add(item);
		
		// File open
		item = new JMenuItem(fileOpenAction);
		menu.add(item);
		
		// File save
		item = new JMenuItem(fileSaveAction);
		menu.add(item);
		
		// File save as
		item = new JMenuItem(new FileSaveAsAction());
		menu.add(item);
		
		menu.add(new JSeparator());
		
		// Export tree
		item = new JMenuItem(exportTreeAction);
		menu.add(item);
		
		// Export tree
		item = new JMenuItem(renderTreeAction);
		menu.add(item);

		menu.add(new JSeparator());
		
		// Quit
		item = new JMenuItem(quitAction);
		menu.add(item);
		
		menubar.add(menu);
		
		/**** setup menu ****/
		menu = new JMenu("Setup");
		menu.setMnemonic('S');
		
		// setup Arbaro
		item = new JMenuItem(new SetupArbaroAction());
		menu.add(item);	
		
		menubar.add(menu);
		
		
		/**** help menu ****/
		menu = new JMenu("Help");
		menu.setMnemonic('H');
		
//		// help paramter
//		item = new JMenuItem("Parameter");
//		item.setMnemonic('P');
//		item.addActionListener(new HelpParameterListener());
//		menu.add(item);	
		
		// help about
		item = new JMenuItem(new HelpAboutAction());
		menu.add(item);
		
		menubar.add(menu);
		
		frame.setJMenuBar(menubar);
	}
	
	void setModified(boolean mod) {
		modified = mod;
		params.getParamEditing().enableDisable();
	}
	
	class FileNewAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public FileNewAction() {
			super("New",createImageIcon("images/actions/New24.png","New"));
        	putValue(SHORT_DESCRIPTION, "New file");
        	putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		}
		public void actionPerformed(ActionEvent e) {
			valueEditor.stopEditing();
			
			// ask if should save when modified...
			if (! shouldSave()) return;

/*			AbstractParam.loading=true; 
			params.clearParams(); */
			params = new ParamManager();
			setModified(false);
/*			AbstractParam.loading=false; */

			groupsView.fireStateChanged();
			/*params.prepare(13); */
			frame.setTitle("Arbaro ["+params.getParamEditing().getSpecies()+"]");
			
			// draw new tree
			try {
				previewTree.setParamManager(params);
				previewTree.remake(true);
			} catch (ParamException err) {
				setModified(false);
				JOptionPane.showMessageDialog(frame,err.getMessage(),
						"Parameter Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception err) {
				System.err.println(err);
				err.printStackTrace();
			}
		}
	};
	
	class FileOpenAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public FileOpenAction() {
			super("Open...",createImageIcon("images/actions/Open24.png","Open"));
        	putValue(SHORT_DESCRIPTION, "Open file");
        	putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		}
		
		public void actionPerformed(ActionEvent e) {
			valueEditor.stopEditing();
			
			// ask if should saved
			if (! shouldSave()) return;
			
			int returnVal = fileChooser.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				System.err.println("opening file: " +
						fileChooser.getSelectedFile().getName());
				try {
					/*AbstractParam.loading=true;
					params.clearParams();
					*/
					treefile = fileChooser.getSelectedFile();
					// read parameters
					params.getParamReader().readFromXML(new FileInputStream(treefile));
					//AbstractParam.loading=false;
					setModified(false);

					groupsView.fireStateChanged();
					//params.prepare(13); 
					frame.setTitle("Arbaro ["+params.getParamEditing().getSpecies()+"]");

					// draw opened tree
					previewTree.setParamManager(params);
					previewTree.remake(true);
					
				} catch (ParamException err) {
					setModified(false);
					ShowException.msgBox(frame,"Parameter Error",err);
				} catch (FileNotFoundException err) {
					ShowException.msgBox(frame,"File not found",err);
				} catch (Exception err) {
					Console.printException(err);
					ShowException.msgBox(frame,"Error opening file",err);
				}

			}	
		}
	};
	
	boolean shouldSave() {
		if (modified) {
			int result = JOptionPane.showConfirmDialog(frame,
					"Some parameters are modified. Should the tree definition be saved?",
					"Tree definition modified",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);  
			if (result == JOptionPane.YES_OPTION) {
				if (treefile != null) {
					return fileSave();
				} else {
					return fileSaveAs();
				}
			}
			return (result != JOptionPane.CANCEL_OPTION);
		} else
			return true; // not modified, can proceed
	}
	
	
	boolean fileSaveAs() {
		int returnVal = fileChooser.showSaveDialog(frame);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			treefile = fileChooser.getSelectedFile();
			return fileSave();
		}
		return false;
	}
	
	boolean fileSave() {
		System.err.println("saving to file: " +
				fileChooser.getSelectedFile().getName());
		try {
			PrintWriter out = new PrintWriter(new FileWriter(treefile));
			params.getParamWriter().toXML(out);
			setModified(false);
			return true;
		} catch (ParamException err) {
			JOptionPane.showMessageDialog(frame,err.getMessage(),
					"Parameter Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (FileNotFoundException err) {
			JOptionPane.showMessageDialog(frame,err.getMessage(),
					"File not found",
					JOptionPane.ERROR_MESSAGE);
		}
		catch (IOException err) {
			JOptionPane.showMessageDialog(frame,err.getMessage(),
					"Output error",
					JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}	
	
	class FileSaveAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public FileSaveAction() {
			super("Save",createImageIcon("images/actions/Save24.png","Save"));
        	putValue(SHORT_DESCRIPTION, "Save file");
        	putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
		}
		
		public void actionPerformed(ActionEvent e) {
			valueEditor.stopEditing();
			
			if (treefile != null) {
				fileSave();
			} else {
				fileSaveAs();
			}
		}
	};
	
	class FileSaveAsAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public FileSaveAsAction() {
			super("Save as...",createImageIcon("images/actions/SaveAs24.png","SaveAs"));
        	putValue(SHORT_DESCRIPTION, "Save file as");
        	putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
		}
		
		public void actionPerformed(ActionEvent e) {
			valueEditor.stopEditing();
			
			fileSaveAs();
		}
	}
	
	class ExportTreeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ExportTreeAction() {
			super("Export tree...",createImageIcon("images/actions/Create24.png","Export"));
        	putValue(SHORT_DESCRIPTION, "Create and export tree");
        	putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
		}
		public void actionPerformed(ActionEvent e) {
			valueEditor.stopEditing();
			new ExportDialog(frame,seed,/*ExporterFactory,*/params,config,false);
		}
	}

	class RenderTreeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public RenderTreeAction() {
			super("Render tree...",createImageIcon("images/actions/Render24.png","Render"));
        	putValue(SHORT_DESCRIPTION, "Create, export and render tree");
        	putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		}
		public void actionPerformed(ActionEvent e) {
			valueEditor.stopEditing();
			new ExportDialog(frame,seed,/*exporterFactory,*/params,config,true);
		}
	}
	
	
	class SetupArbaroAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SetupArbaroAction() {
			super("Setup...",createImageIcon("images/actions/Preferences24.png","Setup"));
        	putValue(SHORT_DESCRIPTION, "Setup Arbaro");
        	putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
		}

		public void actionPerformed(ActionEvent e) {
			new CfgDialog(Workplace.this,frame,config);
		}
	}
	
	class QuitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public QuitAction() {
			super("Quit",null);
        	putValue(SHORT_DESCRIPTION, "Quit Arbaro");
        	putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_Q));
		}
		
		public void actionPerformed(ActionEvent e) {
			valueEditor.stopEditing();
			
			// ask if values should be saved
			if (! shouldSave()) return;
			frame.dispose();
		}

	}
	
//	class HelpParameterListener implements ActionListener {
//		public void actionPerformed(ActionEvent e) {
//			// Component c = frame.getMostRecentFocusOwner();
//			// System.err.println(c.getClass());
//// FIXME
////			if (lastFocused.getClass() == ParamField.class) {
////				JOptionPane.showMessageDialog(frame, 
////						"<html>"+((ParamField)lastFocused).param.getLongDesc()+"</html>",
////						"Parameter description",JOptionPane.INFORMATION_MESSAGE);
////			}
//		}
//	}
	
	class HelpAboutAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public HelpAboutAction() {
			super("About Arbaro...",createImageIcon("images/actions/About24.png","About"));
        	putValue(SHORT_DESCRIPTION, "About Arbaro");
        	putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(frame, net.sourceforge.arbaro.feedback.ProgramInfo.getInfo(),
					"About Arbaro",JOptionPane.INFORMATION_MESSAGE,aboutIcon);
		}
	}
}

/****************** SpeciesField ************************/

//class SpeciesField extends JTextField {
//	
//	Tree tree;
//	
//	public SpeciesField(int width, Tree tr) {
//		super(width);
//		
//		tree = tr;
//		setText(tree.getSpecies());
//		setToolTipText("tree species name, used for object names in the POVRay file");
//		
//		addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				tree.setSpecies(getText());
//			}
//		});
//		addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				tree.setSpecies(getText());
//			}
//		});
//		// add ChangeListener to set species name if changed in the tree
//		tree.params.addChangeListener(new ChangeListener() {
//			public void stateChanged(ChangeEvent e) {
//				setText(tree.getSpecies());
//			}
//		});
//	}
//	
//}
