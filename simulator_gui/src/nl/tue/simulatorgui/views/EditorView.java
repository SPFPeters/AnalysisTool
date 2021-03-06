package nl.tue.simulatorgui.views;

import javax.swing.JPanel;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import nl.tue.simulatorgui.controllers.EditorController;
import nl.tue.simulatorgui.core.Environment;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class EditorView extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	EditorController controller;
	private JTextField txtFile;
	private JTextField txtLength;
	private JTextField txtReplications;
	private JTextField txtWarmup;
	private JCheckBox chkQueueing;
	private String selectedFile;

	public EditorView(EditorController controller){
		setPreferredSize(new Dimension(660, 510));
		setMinimumSize(new Dimension(660, 510));
		this.controller = controller;
		setLayout(null);

		//File entry field
		JLabel lblFile = new JLabel("File:");
		lblFile.setBounds(30, 54, 160, 16);
		add(lblFile);

		txtFile = new JTextField();
		txtFile.setEnabled(false);
		txtFile.setEditable(false);
		txtFile.setBounds(160, 48, 286, 28);
		add(txtFile);
		txtFile.setColumns(10);

		JButton btnFile = new JButton("...");
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectFile()){
					controller.fileChanged();					
				}
			}
		});
		btnFile.setBounds(460, 48, 32, 28);
		add(btnFile);

		//Simulation length entry field
		JLabel lblLength = new JLabel("Simulation length:");
		lblLength.setBounds(30, 94, 160, 16);
		add(lblLength);

		txtLength = new JTextField();
		txtLength.setEnabled(true);
		txtLength.setEditable(true);
		txtLength.setBounds(160, 88, 286, 28);
		add(txtLength);
		txtLength.setColumns(7);
		((PlainDocument) txtLength.getDocument()).setDocumentFilter(new MyIntFilter(this));

		//Replications entry field
		JLabel lblReplications = new JLabel("Replications:");
		lblReplications.setBounds(30, 134, 160, 16);
		add(lblReplications);

		txtReplications = new JTextField();
		txtReplications.setEnabled(true);
		txtReplications.setEditable(true);
		txtReplications.setBounds(160, 128, 286, 28);
		add(txtReplications);
		txtReplications.setColumns(7);
		((PlainDocument) txtReplications.getDocument()).setDocumentFilter(new MyIntFilter(this));

		//Warmup entry field
		JLabel lblWarmup = new JLabel("Warmup time:");
		lblWarmup.setBounds(30, 174, 160, 16);
		add(lblWarmup);

		txtWarmup = new JTextField();
		txtWarmup.setEnabled(true);
		txtWarmup.setEditable(true);
		txtWarmup.setBounds(160, 168, 286, 28);
		add(txtWarmup);
		txtWarmup.setColumns(7);
		((PlainDocument) txtWarmup.getDocument()).setDocumentFilter(new MyIntFilter(this));

		//Warmup entry field
		JLabel lblQueueing = new JLabel("Use queueing model:");
		lblQueueing.setBounds(30, 214, 160, 16);
		add(lblQueueing);

		chkQueueing = new JCheckBox();
		chkQueueing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.fileChanged();					
			}
		});
		chkQueueing.setBounds(160, 214, 16, 16);
		add(chkQueueing);
	}

	public EditorController getController() {
		return controller;
	}

	private boolean selectFile(){
		final JFileChooser fc = new JFileChooser();
		String lastFolder = Environment.getProperties().getLastFolder();
		if (lastFolder != null){
			fc.setCurrentDirectory(new File(lastFolder));
		}
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("BPMN 2.0 XML (.bpmn)", "bpmn");
		fc.addChoosableFileFilter(filter);
		int returnVal = Environment.getMainController().showDialog(fc, "Open");
		if (returnVal == JFileChooser.APPROVE_OPTION){
			selectedFile = fc.getSelectedFile().getAbsolutePath();
			txtFile.setText(fc.getSelectedFile().getName());
			Environment.getProperties().setLastFolder(fc.getSelectedFile().getParent());
			return true;
		}
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.fileChanged();
	}

	public String getSelectedFile() {
		if (selectedFile == null){
			return "";
		}
		return selectedFile;
	}

	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
		File f = new File(selectedFile);
		txtFile.setText(f.getName());
	}

	public void setSimulationLength(long length){
		txtLength.setText(Long.toString(length));
	}

	public long getSimulationLength(){
		Long result = 0L;
		try{
			result = Long.parseLong(txtLength.getText());
		}catch (Exception e){}
		return result;
	}

	public long getReplications(){
		Long result = 0L;
		try{
			result = Long.parseLong(txtReplications.getText());
		}catch (Exception e){}
		return result;
	}

	public void setReplications(long replications){
		txtReplications.setText(Long.toString(replications));
	}

	public long getWarmup(){
		Long result = 0L;
		try{
			result = Long.parseLong(txtWarmup.getText());
		}catch (Exception e){}
		return result;
	}

	public void setWarmup(long warmup){
		txtWarmup.setText(Long.toString(warmup));
	}
	
	public void setQueueing(boolean useQueueing) {
		chkQueueing.setSelected(useQueueing);
	}
	
	public boolean getQueueing() {
		return chkQueueing.isSelected();
	}

	class MyIntFilter extends DocumentFilter {

		EditorView view;

		public MyIntFilter(EditorView view){
			this.view = view;
		}
		
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);

			if (test(sb.toString())) {
				super.insertString(fb, offset, string, attr);
				view.getController().fileChanged();
			} else {
				// warn the user and don't allow the insert
			}
		}

		private boolean test(String text) {
			if (text.length() == 0){
				return true;
			}
			try {
				Integer.parseInt(text);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text);

			if (test(sb.toString())) {
				super.replace(fb, offset, length, text, attrs);
				view.getController().fileChanged();
			} else {
				// warn the user and don't allow the insert
			}

		}

		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.delete(offset, offset + length);

			if (test(sb.toString())) {
				super.remove(fb, offset, length);
				view.getController().fileChanged();
			} else {
				// warn the user and don't allow the insert
			}

		}
	}
}
