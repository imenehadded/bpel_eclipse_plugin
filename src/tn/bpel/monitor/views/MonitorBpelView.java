package tn.bpel.monitor.views;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.*;

import bpel.transformator.Transformator;

import tn.bpel.monitor.Activator;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class MonitorBpelView extends ViewPart {

	public static final String ID = "tn.bpel.monitor.views.MonitorBpelView";

	private Text textWsdlLocation = null;
	private Text textExpression = null;
	private ComboViewer comboProjectList = null;

	private Transformator transformator = null;
	private Label lbproject_info;

	public MonitorBpelView() {
	}

	public void createPartControl(final Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		parent.setLayout(new GridLayout(3, false));

		// ################# RAW 1 ##################
		Label labelProjectList = new Label(parent, SWT.NONE);
		labelProjectList.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		labelProjectList.setText("Bpel projects :");

		comboProjectList = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboProjectList.getCombo();
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 286;
		combo.setLayoutData(gd_combo);
		comboProjectList.setContentProvider(ArrayContentProvider.getInstance());

		comboProjectList.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {

				return ((IProject) element).getName();
			}
		});

		comboProjectList.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {

				IProject projectSelectd = (IProject) comboProjectList.getStructuredSelection().getFirstElement();

				String projectPath = projectSelectd.getLocation().toString();

				try {
					transformator = new Transformator(projectPath);
					lbproject_info.setText("");

				} catch (Exception e) {

					lbproject_info.setText(e.toString());

				}

			}
		});
		comboProjectList.setInput(this.getProjectList());

		lbproject_info = new Label(parent, SWT.NONE);

		// ################# RAW 2 ##################
		Label label = new Label(parent, SWT.NONE);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label.setText("Monitor wsdl location:");

		textWsdlLocation = new Text(parent, SWT.BORDER | SWT.SINGLE);
		GridData gridData = new GridData();
		gridData.widthHint = 388;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		textWsdlLocation.setLayoutData(gridData);
		textWsdlLocation.setText("");

		Button selectFile = new Button(parent, SWT.PUSH);
		selectFile.setText("Select...");

		selectFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(parent.getShell(), SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath("C:/");
				String[] filterExt = { "*.wsdl" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				textWsdlLocation.setText(selected);
			}
		});

		// ################# RAW 3 ##################

		Label labelExpression = new Label(parent, SWT.NONE);
		labelExpression.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		labelExpression.setText("Expression:");

		textExpression = new Text(parent, SWT.BORDER | SWT.SINGLE);
		textExpression.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				getExpression();
			}
		});
		textExpression.setEditable(false);

		textExpression.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));

		Button selectAction = new Button(parent, SWT.PUSH);
		selectAction.setText("Actions");

		selectAction.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				 getExpression();


			}
		});
		new Label(parent, SWT.NONE);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite.widthHint = 82;
		composite.setLayoutData(gd_composite);

		// ################# RAW 4 ##################

		Button button = new Button(composite, SWT.PUSH);
		button.setText("Run");

		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setSelection(true);
		btnNewButton.setText("Clean");
		new Label(parent, SWT.NONE);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String expression = textExpression.getText();
				String monitor_wsdl_file = textWsdlLocation.getText();

				run(expression, monitor_wsdl_file);

			}

		});

	}

	private void run(String expression, String monitor_wsdl_file) {

		Shell shell = comboProjectList.getCombo().getShell();

		try {

			transformator.set_wsdl_monitoring_file(monitor_wsdl_file, expression);

			transformator.add_monitor_service(monitor_wsdl_file, expression);

			MessageDialog.openInformation(shell, "Monitor bpel View", "Monitor has been added in bpel file.");
		} catch (Exception e) {
			errorDialogWithStackTrace("run error", e);
		}

	}

	public static void errorDialogWithStackTrace(String msg, Throwable t) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);

		final String trace = sw.toString(); // stack trace as a string

		// Temp holder of child statuses
		List<Status> childStatuses = new ArrayList<Status>();

		// Split output by OS-independend new-line
		for (String line : trace.split(System.getProperty("line.separator"))) {
			// build & add status
			childStatuses.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, line));
		}

		MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, childStatuses.toArray(new Status[] {}),
				t.getLocalizedMessage(), t);

		ErrorDialog.openError(null, "Error", msg, ms);
	}

	private IProject[] getProjectList() {

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		return projects;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private void getExpression() {

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		ExpressionView dialog = new ExpressionView(shell, transformator);

		dialog.open();
 
		if (dialog.expression_str == null)
			return;
		
		textExpression.setText(dialog.expression_str);
	}
}
