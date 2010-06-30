package edu.cmu.cs.stage3.swing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import edu.cmu.cs.stage3.alice.core.Address;
import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.lego.NXTArm;

import javax.bluetooth.BluetoothStateException;

public class AddressScan extends edu.cmu.cs.stage3.swing.ContentPane {
	javax.swing.JLabel label = new javax.swing.JLabel("Address:");
	javax.swing.JTextField typeBox = new javax.swing.JTextField("", 4);
	javax.swing.JButton findAndConnect = new javax.swing.JButton(
			"Find and Connect");
	javax.swing.JButton okayButton = new javax.swing.JButton("Not visible");
	javax.swing.JButton cancelButton = new javax.swing.JButton("Cancel");

	Address selectedAddress = new Address();

	public static void main(String args[]) {
		AddressScan a = new AddressScan(new Create());
		javax.swing.JFrame frame = new javax.swing.JFrame(a.getTitle());
		frame.setContentPane(a);
		frame.pack();
		frame.setVisible(true);
	}

	public AddressScan(Create robot) {
		findAndConnect.setEnabled(false);
		add(label);
		typeBox.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				if(typeBox.getText().length()!=4)
					findAndConnect.setEnabled(false);
				else
					findAndConnect.setEnabled(true);
			}
		});

		add(typeBox);

		findAndConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						search();
					}
				});
				t.start();
			}
		});
		add(findAndConnect, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(4, 4, 4, 4), 0, 0));
		add(cancelButton, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(4, 4, 4, 4), 0, 0));
	}
	
	public AddressScan(NXTArm robot) {
		findAndConnect.setEnabled(false);
		add(label);
		typeBox.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				if(typeBox.getText().length()!=4)
					findAndConnect.setEnabled(false);
				else
					findAndConnect.setEnabled(true);
			}
		});

		add(typeBox);

		findAndConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						search();
					}
				});
				t.start();
			}
		});
		add(findAndConnect, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(4, 4, 4, 4), 0, 0));
		add(cancelButton, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(4, 4, 4, 4), 0, 0));
	}

	public Address getSelectedAddress() {
		return selectedAddress;
	}

	public String getTitle() {
		return "Enter four digit address to connect";
	}

	public void addOKActionListener(java.awt.event.ActionListener l) {
		okayButton.addActionListener(l);
	}

	public void removeOKActionListener(java.awt.event.ActionListener l) {
		okayButton.removeActionListener(l);
	}

	public void addCancelActionListener(java.awt.event.ActionListener l) {
		cancelButton.addActionListener(l);
	}

	public void removeCancelActionListener(java.awt.event.ActionListener l) {
		cancelButton.removeActionListener(l);
	}

	public void search() {
		findAndConnect.setEnabled(false);
		cancelButton.setEnabled(false);
		try {
			BluetoothInterface bti = new BluetoothInterface();
			selectedAddress = Address.valueOf(bti.findBamModuleEndingWith(typeBox.getText().toUpperCase()));
			if(selectedAddress==Address.NONE)
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("The robot at address '" + typeBox.getText().toUpperCase() + "' could not be found.", "Not found", JOptionPane.ERROR_MESSAGE); 
			else
				okayButton.doClick();
		} catch(BluetoothStateException bse){
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(
					"A Bluetooth device could not be found on this computer. Make sure a Bluetooth " +
					"device is connected and installed correctly.",
					"Warning: Bluetooth device not found",
					javax.swing.JOptionPane.WARNING_MESSAGE);
		}catch (Exception e) {
		}
		findAndConnect.setEnabled(true);
		cancelButton.setEnabled(true);
	}

}
