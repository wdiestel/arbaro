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

import java.io.*;
import java.awt.*;
import java.awt.event.*;
//import java.util.zip.GZIPOutputStream;
import javax.swing.*;

import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.tree.TreeGenerator;
import net.sourceforge.arbaro.tree.TreeGeneratorFactory;
import net.sourceforge.arbaro.export.*;
import net.sourceforge.arbaro.params.Params;

public class ExportDialog {
	final static int INTERVAL = 500; // 0.5 sec
	// static Tree tree;
	JFrame frame;
	Config config;
	boolean render;

	JPanel mainPanel;
	Progressbar progressbar;

	// Tree tree;
	Params params;
	int seed;
//	ExporterFactory exporterFactory;

	File treefile = null;
	JFileChooser fileChooser;
	JFileChooser sceneFileChooser;
	JFileChooser renderFileChooser;
	JTabbedPane tabbedPane;
	JComboBox formatBox;
	JCheckBox sceneCheckbox;
	JCheckBox renderCheckbox;
	JCheckBox uvStemsCheckbox;
	JCheckBox uvLeavesCheckbox;
	JTextField seedField = new JTextField(6);
	JTextField smoothField = new JTextField(6);
	JTextField fileField;
	JTextField sceneFileField;
	JTextField renderFileField;
	JTextField widthField=new JTextField(6);
	JTextField heightField=new JTextField(6);
	JButton selectSceneFile;
	JButton selectRenderFile;
	Timer timer;
	TreeCreationTask treeCreationTask;
	JButton startButton;
	JButton cancelButton;

	String fileSep = System.getProperty("file.separator");

	public ExportDialog(JFrame parent, int seed, /*ExporterFactory exporterFactory,*/ Params params, Config cfg, boolean render) {

		//tree = tr;
		this.params = params;
		this.config = cfg;
		this.render = render;
//		this.exporterFactory = exporterFactory;
		this.seed = seed;

		frame = new JFrame("Create and export tree");
		frame.setIconImage(parent.getIconImage());
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// FIXME: make this filechoosers static?
		fileChooser = new JFileChooser();

		// FIXME use path from preferences
		fileChooser.setCurrentDirectory(new File(ExporterFactory.getOutputPath()));
//		System.getProperty("user.dir")+fileSep+"pov"));
		sceneFileChooser = new JFileChooser();
		sceneFileChooser.setCurrentDirectory(new File(ExporterFactory.getOutputPath()));
//				System.getProperty("user.dir")+fileSep+"pov"));
		renderFileChooser = new JFileChooser();
		renderFileChooser.setCurrentDirectory(new File(ExporterFactory.getOutputPath()));
//		System.getProperty("user.dir")+fileSep+"pov"));

		timer = new Timer(INTERVAL, new TimerListener());
		treeCreationTask = new TreeCreationTask(frame,config);

		createGUI();
		frame.setVisible(true);
	}

	void createGUI() {
		tabbedPane = new JTabbedPane();

		JComponent exportPanel = createExportPanel();
		tabbedPane.addTab("Export", null, exportPanel,
		                  "Export options");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent renderPanel = createRenderPanel();
		tabbedPane.addTab("Render", null, renderPanel,
        "Render options");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);

		if (render) {
			tabbedPane.setSelectedIndex(1);
		}
		formatSettings(formatBox.getSelectedIndex());

		// buttons
		startButton = new JButton("Start");
		startButton.addActionListener(new StartButtonListener());

		cancelButton = new JButton("Close");
		cancelButton.addActionListener(new CancelButtonListener());

		JPanel buttons = new JPanel();
		buttons.add(startButton);
		buttons.add(cancelButton);

		JPanel panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		panel.setLayout(new BorderLayout());
		panel.add(tabbedPane,BorderLayout.CENTER);
		panel.add(buttons,BorderLayout.SOUTH);

		// add panel to content pane
		Container contentPane = frame.getContentPane();
		contentPane.add(panel,BorderLayout.CENTER);

		// add status line
		progressbar = new Progressbar();
		progressbar.setVisible(false);
		contentPane.add(progressbar,BorderLayout.PAGE_END);

		frame.pack();
	}

	void formatSettings(int outputFormat) {
		switch (outputFormat) {
		case ExporterFactory.POV_MESH:
			fileField.setText(fileChooser.getCurrentDirectory().getPath()
					+fileSep+params.Species+".inc");
			sceneCheckbox.setEnabled(true);
			renderCheckbox.setEnabled(true);
			smoothField.setEnabled(true);
			tabbedPane.setEnabledAt(1,true);
			uvStemsCheckbox.setEnabled(true);
			uvLeavesCheckbox.setEnabled(true);
			sceneFileField.setText(sceneFileChooser.getCurrentDirectory().getPath()
				+fileSep+params.Species+".pov");
			renderFileField.setText(renderFileChooser.getCurrentDirectory().getPath()
				+fileSep+params.Species+".png");
		break;

		case ExporterFactory.POV_CONES:
			fileField.setText(fileChooser.getCurrentDirectory().getPath()
					+fileSep+params.Species+".inc");
			sceneCheckbox.setEnabled(true);
			renderCheckbox.setEnabled(true);
			smoothField.setEnabled(false);
			tabbedPane.setEnabledAt(1,true);
			uvStemsCheckbox.setEnabled(false);
			uvLeavesCheckbox.setEnabled(false);
			sceneFileField.setText(sceneFileChooser.getCurrentDirectory().getPath()
				+fileSep+params.Species+".pov");
			renderFileField.setText(renderFileChooser.getCurrentDirectory().getPath()
				+fileSep+params.Species+".png");
		break;

		case ExporterFactory.RIB:
			fileField.setText(fileChooser.getCurrentDirectory().getPath()
					+fileSep+params.Species+".rib");
			sceneCheckbox.setEnabled(true);
			renderCheckbox.setEnabled(true);
			smoothField.setEnabled(true);
			tabbedPane.setEnabledAt(1,true);
			uvStemsCheckbox.setEnabled(true);
			uvLeavesCheckbox.setEnabled(true);
			sceneFileField.setText(sceneFileChooser.getCurrentDirectory().getPath()
				+fileSep+params.Species+"_scene.rib");
			renderFileField.setText(renderFileChooser.getCurrentDirectory().getPath()
				+fileSep+params.Species+".tif");
		break;

		case ExporterFactory.DXF:
			fileField.setText(fileChooser.getCurrentDirectory().getPath()
					+fileSep+params.Species+".dxf");
			sceneCheckbox.setSelected(false);
			sceneCheckbox.setEnabled(false);
			renderCheckbox.setSelected(false);
			renderCheckbox.setEnabled(false);
			smoothField.setEnabled(true);
			tabbedPane.setEnabledAt(1,false);
			uvStemsCheckbox.setEnabled(false);
			uvLeavesCheckbox.setEnabled(false);
		break;

		case ExporterFactory.OBJ:
			fileField.setText(fileChooser.getCurrentDirectory().getPath()
					+fileSep+params.Species+".obj");
			sceneCheckbox.setSelected(false);
			sceneCheckbox.setEnabled(false);
			renderCheckbox.setSelected(false);
			renderCheckbox.setEnabled(false);
			smoothField.setEnabled(true);
			tabbedPane.setEnabledAt(1,false);
			uvStemsCheckbox.setEnabled(true);
			uvLeavesCheckbox.setEnabled(true);
		break;

		}
	}


	JPanel createExportPanel() {

		JPanel panel = new JPanel();

		// create GridBagLayout
		GridBagLayout grid = new GridBagLayout();
		panel.setLayout(grid);

		panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
//		panel.setBorder(BorderFactory.createCompoundBorder(
//				BorderFactory.createRaisedBevelBorder(),
//				BorderFactory.createEmptyBorder(30,30,30,30)));

		// Constraints for the left labels
		GridBagConstraints clabel = new GridBagConstraints();
		clabel.gridx = 0;
		clabel.anchor = GridBagConstraints.WEST;

		// Constraint for the input fields
		GridBagConstraints ctext = new GridBagConstraints();
		ctext.gridx = 1;
		ctext.ipady = 4;
		ctext.anchor = GridBagConstraints.WEST;
		ctext.insets = new Insets(1,5,1,5);

		// Constraint for the choose buttons
		GridBagConstraints cbutton = new GridBagConstraints();
		cbutton.gridx = 2;
		cbutton.anchor = GridBagConstraints.WEST;

		JLabel label;
		int line=-1;

		// export format
		clabel.gridy = ++line;
		label = new JLabel("Export format:");
		grid.setConstraints(label,clabel);
		panel.add(label);

		ctext.gridy = line;
		formatBox = new JComboBox(ExporterFactory.getExportFormats());
		int format = Integer.parseInt(config.getProperty("export.format","0"));
		if (render) {
			for (int i=formatBox.getItemCount()-1; i>=ExporterFactory.DXF; i--) {
				formatBox.removeItemAt(i);
			}
			if (format>=ExporterFactory.DXF) format=ExporterFactory.POV_MESH;
		}
		formatBox.setEditable(false);
		formatBox.setSelectedIndex(format);

		formatBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				formatSettings(formatBox.getSelectedIndex());
			}
		});
		grid.setConstraints(formatBox,ctext);
		panel.add(formatBox);


		// export file path and name
		clabel.gridy = ++line;
		label = new JLabel("Export to file:");
		grid.setConstraints(label,clabel);
		panel.add(label);

		ctext.gridy = line;
		fileField = new JTextField(30);
		fileField.setText(fileChooser.getCurrentDirectory().getPath()
				+fileSep+params.Species+".inc");
		fileField.setMinimumSize(new Dimension(250,19));
		grid.setConstraints(fileField,ctext);
		panel.add(fileField);

		cbutton.gridy = line;
		JButton selectFile = new JButton("Choose...");
		selectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int returnVal = fileChooser.showSaveDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					fileField.setText(fileChooser.getSelectedFile().getPath());
				}
			}
		});
		grid.setConstraints(selectFile,cbutton);
		panel.add(selectFile);

		// UV-coordinates
		clabel.gridy = ++line;
		label = new JLabel("UV-coordinates:");
		grid.setConstraints(label,clabel);
		panel.add(label);

		ctext.gridy = line;
		uvStemsCheckbox = new JCheckBox("for Stems");
		uvStemsCheckbox.setSelected(ExporterFactory.getOutputStemUVs());
		uvLeavesCheckbox = new JCheckBox("for Leaves");
		uvLeavesCheckbox.setSelected(ExporterFactory.getOutputLeafUVs());
		JPanel uv = new JPanel();
		uv.add(uvStemsCheckbox);
		uv.add(uvLeavesCheckbox);
		grid.setConstraints(uv,ctext);
		panel.add(uv);

		// POV scene file
		clabel.gridy = ++line;
		sceneCheckbox = new JCheckBox("Scene file:");
		sceneCheckbox.setSelected(render);
		sceneCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				sceneFileField.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				selectSceneFile.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				renderCheckbox.setSelected(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		grid.setConstraints(sceneCheckbox,clabel);
		panel.add(sceneCheckbox);

		ctext.gridy = line;
		sceneFileField = new JTextField(30);
		sceneFileField.setEnabled(sceneCheckbox.isSelected());
		sceneFileField.setText(sceneFileChooser.getCurrentDirectory().getPath()
				+fileSep+params.Species+".pov");
		sceneFileField.setMinimumSize(new Dimension(250,19));
		grid.setConstraints(sceneFileField,ctext);
		panel.add(sceneFileField);

		cbutton.gridy = line;
		selectSceneFile = new JButton("Choose...");
		selectSceneFile.setEnabled(sceneCheckbox.isSelected());
		selectSceneFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int returnVal = sceneFileChooser.showSaveDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					sceneFileField.setText(sceneFileChooser.getSelectedFile().getPath());
				}
			}
		});
		grid.setConstraints(selectSceneFile,cbutton);
		panel.add(selectSceneFile);

		// random seed
		clabel.gridy = ++line;
		label = new JLabel("Seed:");
		grid.setConstraints(label,clabel);
		panel.add(label);

		ctext.gridy = line;
		seedField.setText(""+seed);
		seedField.setMinimumSize(new Dimension(80,19));
		grid.setConstraints(seedField,ctext);
		panel.add(seedField);

		// smooth value
		clabel.gridy = ++line;
		label = new JLabel("Smooth value:");
		grid.setConstraints(label,clabel);
		panel.add(label);

		ctext.gridy = line;
		smoothField.setText(params.getParam("Smooth").toString());
		smoothField.setMinimumSize(new Dimension(80,19));
		grid.setConstraints(smoothField,ctext);
		panel.add(smoothField);

		return panel;
	}


	JPanel createRenderPanel() {
		JPanel panel = new JPanel();

		// create GridBagLayout
		GridBagLayout grid = new GridBagLayout();
		panel.setLayout(grid);
		panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
//		panel.setBorder(BorderFactory.createCompoundBorder(
//				BorderFactory.createRaisedBevelBorder(),
//				BorderFactory.createEmptyBorder(30,30,30,30)));

		// Constraints for the left labels
		GridBagConstraints clabel = new GridBagConstraints();
		clabel.gridx = 0;
		clabel.anchor = GridBagConstraints.WEST;

		// Constraint for the input fields
		GridBagConstraints ctext = new GridBagConstraints();
		ctext.gridx = 1;
		ctext.ipady = 4;
		ctext.anchor = GridBagConstraints.WEST;
		ctext.insets = new Insets(1,5,1,5);

		// Constraint for the choose buttons
		GridBagConstraints cbutton = new GridBagConstraints();
		cbutton.gridx = 2;
		cbutton.anchor = GridBagConstraints.WEST;

		JLabel label;
		int line=-1;

		// path and filename of rendered image
		clabel.gridy = ++line;
		renderCheckbox = new JCheckBox("Render scene to:");
		renderCheckbox.setSelected(render);
		renderCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				renderFileField.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				selectRenderFile.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		grid.setConstraints(renderCheckbox,clabel);
		panel.add(renderCheckbox);

		ctext.gridy = line;
		renderFileField = new JTextField(30);
		renderFileField.setEnabled(renderCheckbox.isSelected());
		renderFileField.setText(renderFileChooser.getCurrentDirectory().getPath()
				+fileSep+params.Species+".png");
		renderFileField.setMinimumSize(new Dimension(250,19));
		grid.setConstraints(renderFileField,ctext);
		panel.add(renderFileField);

		cbutton.gridy = line;
		selectRenderFile = new JButton("Choose...");
		selectRenderFile.setEnabled(renderCheckbox.isSelected());
		selectRenderFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int returnVal = renderFileChooser.showSaveDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					renderFileField.setText(renderFileChooser.getSelectedFile().getPath());
				}
			}
		});
		grid.setConstraints(selectRenderFile,cbutton);
		panel.add(selectRenderFile);

		// render width
		clabel.gridy = ++line;
		label = new JLabel("Image width:");
		grid.setConstraints(label,clabel);
		panel.add(label);

		ctext.gridy = line;
		widthField.setText(""+ExporterFactory.getRenderW());
		widthField.setMinimumSize(new Dimension(80,19));
		grid.setConstraints(widthField,ctext);
		panel.add(widthField);

		// render height
		clabel.gridy = ++line;
		label = new JLabel("Image height:");
		grid.setConstraints(label,clabel);
		panel.add(label);

		ctext.gridy = line;
		heightField.setText(""+ExporterFactory.getRenderH());
		heightField.setMinimumSize(new Dimension(80,19));
		grid.setConstraints(heightField,ctext);
		panel.add(heightField);

		return panel;
	}

	class StartButtonListener implements ActionListener {
		// creates the tree and writes to file when button pressed
		public void actionPerformed(ActionEvent e) {
			// System.err.println("seed "+seedField.getText());
			// System.err.println("smooth "+smoothField.getText());
			// System.err.println("0Branches "+tree.params.getParam("0Branches").getValue());

			// get seed, output parameters

			// FIXME set progress, verbose, debug here?
			TreeGenerator treeGenerator = new ShieldedGUITreeGenerator(frame,
					TreeGeneratorFactory.createTreeGenerator(params));
//			ExporterFactory exporterFactory = new ExporterFactory();

			try{
				treeGenerator.setSeed(Integer.parseInt(seedField.getText()));
				treeGenerator.setParam("Smooth",smoothField.getText());
				ExporterFactory.setRenderW(Integer.parseInt(widthField.getText()));
				ExporterFactory.setRenderH(Integer.parseInt(heightField.getText()));
				ExporterFactory.setExportFormat(formatBox.getSelectedIndex());
				ExporterFactory.setOutputPath(fileField.getText());
				ExporterFactory.setImageFilePath(renderFileField.getText());
				ExporterFactory.setOutputStemUVs(uvStemsCheckbox.isSelected());
				ExporterFactory.setOutputLeafUVs(uvLeavesCheckbox.isSelected());
				//FIXME fileChooser.getPath() ???
				//tree.setOutputPath(fileField.getText());
			} catch (Exception exc) {
				net.sourceforge.arbaro.export.Console.printException(exc);
				ShowException.msgBox(frame,"Export initialization error",exc);
			}
			// setup progress dialog
//			progressMonitor = new ProgressMonitor(frame,"","",0,100);
//			progressMonitor.setProgress(0);
//			progressMonitor.setMillisToDecideToPopup(1*INTERVAL);

			progressbar.setVisible(true);

			startButton.setEnabled(false);
			cancelButton.setText("Cancel");

			// start tree creation
			System.err.println("start creating tree and writing to "+fileField.getText());

			File incfile = new File(fileField.getText());
			File povfile = null;
			if (sceneCheckbox.isSelected()) povfile = new File(sceneFileField.getText());
			String imgFilename = null;
			if (renderCheckbox.isSelected()) imgFilename = renderFileField.getText();
			treeCreationTask.start(treeGenerator,/*exporterFactory,*/incfile,povfile,imgFilename); //fileChooser.getSelectedFile());
			timer.start();
		}
	}

	class CancelButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			if (treeCreationTask.notActive())
				frame.dispose();
			else {
				treeCreationTask.stop();
//				progressMonitor.close();
				Toolkit.getDefaultToolkit().beep();
				timer.stop();
				startButton.setEnabled(true);
				startButton.setText("Restart");
				cancelButton.setText("Close");
				progressbar.setVisible(false);
			}
		}
	}

	class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			// System.err.println("timer event "+createTreeTask.getProgress());
			if (/*progressMonitor.isCanceled() ||*/ treeCreationTask.notActive())
			{
				treeCreationTask.stop();
//				progressMonitor.close();
				progressbar.setProgress(100);
				progressbar.setNote("Ready");
				Toolkit.getDefaultToolkit().beep();
				timer.stop();
				startButton.setEnabled(true);
				startButton.setText("Restart");
				cancelButton.setText("Close");
			} else {
//				progressMonitor.setProgress((int)(100*createTreeTask.getProgress()));
//				progressMonitor.setNote(createTreeTask.getProgressMsg());

				progressbar.setProgress(treeCreationTask.getProgress());
				progressbar.setNote(treeCreationTask.getProgressMsg());
			}
		}
	}
}

class Progressbar extends JPanel {
	private static final long serialVersionUID = 1L;

	JLabel label;
	JProgressBar progressbar;

	public Progressbar() {
		super(new BorderLayout());
		((BorderLayout)getLayout()).setHgap(20);

		setBorder(BorderFactory.createEmptyBorder(10,5,5,5));

		label = new JLabel("Creating tree structure");
    	Font font = label.getFont().deriveFont(Font.PLAIN,12);
    	label.setFont(font);
		add(label,BorderLayout.WEST);

		progressbar = new JProgressBar(0,100);
		progressbar.setValue(-1);
		progressbar.setStringPainted(true);
		progressbar.setString("");
		progressbar.setIndeterminate(true);
		add(progressbar,BorderLayout.CENTER);
	}

	public void setProgress(int val) {
//		System.err.println("setProg "+val);
		if (val<=0) {
			progressbar.setIndeterminate(true);
			progressbar.setString("");
		} else {
			progressbar.setIndeterminate(false);
			progressbar.setValue(val);
			progressbar.setString(""+val+"%");
		}
	}

	public void setNote(String note) {
		label.setText(note);
	}
}

/* this class actually creates the tree and saves it to a POV file
 */
class TreeCreationTask {
	TreeGenerator treeGenerator;
//	ExporterFactory exporterFactory;
	Progress progress;
	//Tree tmptree;
	PrintWriter writer;
	File scene_file = null;
	PrintWriter scenewriter = null;
	String renderFilename = null;
	boolean isNotActive;
	String renderExe;
	Component parent;

	final class TreeWorker extends SwingWorker {

		public Object construct() {
			return new DoTask(parent);
		}

		public void finished() {
			// may be this should be done in DoTask instead?
			isNotActive = true;
		}
	};

	TreeWorker worker;

	public TreeCreationTask(Component parent, Config config) {
		this.parent = parent;
		renderExe = config.getProperty("povray.executable");
		if (renderExe == null) {
			System.err.println("Warning: Renderer executable not set up, trying \""+
			Config.defaultRendererExe()+"\" without path");
			renderExe = Config.defaultRendererExe();
		}
		isNotActive=true;
	};

	public void start(TreeGenerator treeFactory/*, ExporterFactory exporterFactory*/,
			File outFile, File sceneFile, String imgFilename) {
		// create new Tree copying the parameters of tree
		try {
			this.treeGenerator = treeFactory;
//			this.exporterFactory = exporterFactory;

			//writer = new PrintWriter(new GZIPOutputStream(new FileOutputStream(outFile)));
			writer = new PrintWriter(new FileWriter(outFile));
			if (sceneFile != null) {
				scene_file = sceneFile;
				scenewriter = new PrintWriter(new FileWriter(sceneFile));
			}
			renderFilename = imgFilename;

			isNotActive = false;

			worker = new TreeWorker();
			worker.start();

		} catch (Exception e) {
			net.sourceforge.arbaro.export.Console.printException(e);
			//ShowException(frame,"Tree creation initialization error",e);
		}
	}

	int getProgress() {
		return progress.getPercent();
	}

	String getProgressMsg() {
		return progress.getPhase();
	}

	void stop() {
		if (worker != null) {
			System.err.println("stop tree creation...");
			worker.interrupt();
			isNotActive = true;
			worker = null;
		}
	}

	boolean notActive() {
		return isNotActive;
	}

	class DoTask {
		void render() {
			try {

				/*String[] cmd;
				if(parent.outputFormat == ExporterFactory.RIB) {
					cmd=new String[2];
					cmd[0]=renderExe;
					cmd[1]=scene_file.getPath();
				} else {
					cmd=new String[6];
					cmd[0]=renderExe;
					cmd[1]="+L"+scene_file.getParent();
					cmd[2]="+w"+ExporterFactory.getRenderW();
					cmd[3]="+h"+ExporterFactory.getRenderH();
					cmd[4]="+o"+renderFilename;
					cmd[5]=scene_file.getPath();
				}*/
				String [] cmd = { renderExe,
						scene_file.getPath() };
						/*"+L"+scene_file.getParent(),
						"+w"+ExporterFactory.getRenderW(),
						"+h"+ExporterFactory.getRenderH(),
						"+o"+renderFilename,
						scene_file.getPath()};*/
				System.err.println(cmd);
				System.err.println("rendering...");
				Process povProc = Runtime.getRuntime().exec(cmd);
				BufferedReader pov_in = new BufferedReader(
						new InputStreamReader(povProc.getErrorStream()));

				String str;
				while ((str = pov_in.readLine()) != null) {
					System.err.println(str);
				}

			} catch (Exception e) {
				System.err.println(e);
				e.printStackTrace(System.err);
			}
		}

		DoTask(Component parent) {
			try {
				progress = new Progress();
				// create the tree
				Tree tree = treeGenerator.makeTree(progress);
//				Params params = treeGenerator.getParams();
				// export the tree
				Exporter exporter = new ShieldedGUIExporter(parent,
						ExporterFactory.createExporter(tree));
				exporter.write(writer,progress);

				// export rendering scene ?
				if (scenewriter != null) {
					exporter = new ShieldedGUIExporter(parent,
							ExporterFactory.createSceneExporter(tree));

					exporter.write(scenewriter,progress);
				}

				// render scene ?
				if (renderFilename != null && renderFilename.length()>0) {
					progress.beginPhase("Rendering tree",-1);
					render();
					progress.endPhase();
				}

			} catch (Exception err) {
				System.err.println(err);
				err.printStackTrace(System.err);
			}
		}
	};
}









