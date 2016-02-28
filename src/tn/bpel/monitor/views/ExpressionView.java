package tn.bpel.monitor.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpel.transformator.Action;
import bpel.transformator.Transformator;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class ExpressionView extends TitleAreaDialog {

	private Text txtExpression;
	private Transformator transformator;
	private List<Action> listActions;

	private List<String> expression = new ArrayList<String>();

	public ExpressionView(Shell parentShell, Transformator transformator) {
		super(parentShell);
		this.transformator = transformator;

	}

	public String expression_str;
	private TableViewer table;
	private Table table_1;

	private String getExpression() {
		String exp = "";
		for (String itm : this.expression)
			exp += itm;
		return exp;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Expression Editor");
		// setMessage("This is a TitleAreaDialog",
		// IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Expression Editor");
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(null);

		txtExpression = new Text(area, SWT.BORDER);
		txtExpression.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		txtExpression.setEditable(false);
		txtExpression.setToolTipText("");
		txtExpression.setBounds(10, 2, 569, 42);
		txtExpression.setText("");

		SelectionAdapter mevn = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();
				append_to_expression(btn.getText());
			}
		};

		Button button = new Button(area, SWT.NONE);
		button.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));

		button.addSelectionListener(mevn);
		button.setBounds(10, 84, 54, 35);
		button.setText("(");

		Button button_1 = new Button(area, SWT.NONE);
		button_1.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		button_1.addSelectionListener(mevn);
		button_1.setText(")");
		button_1.setBounds(70, 84, 54, 35);

		Button button_4 = new Button(area, SWT.NONE);
		button_4.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		button_4.addSelectionListener(mevn);
		button_4.setText("*");
		button_4.setBounds(10, 125, 54, 35);

		Button button_5 = new Button(area, SWT.NONE);
		button_5.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		button_5.addSelectionListener(mevn);
		button_5.setText("^");
		button_5.setBounds(70, 125, 54, 35);

		Button button_6 = new Button(area, SWT.NONE);
		button_6.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		button_6.addSelectionListener(mevn);
		button_6.setText(".");
		button_6.setBounds(10, 166, 54, 35);

		Button button_7 = new Button(area, SWT.NONE);
		button_7.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		button_7.addSelectionListener(mevn);
		button_7.setText("+");
		button_7.setBounds(70, 166, 54, 35);

		Button btnReset = new Button(area, SWT.NONE);
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (expression.size() == 0)
					return;
				expression.remove(expression.size() - 1);

				txtExpression.setText(getExpression());
			}
		});
		btnReset.setBounds(45, 52, 75, 25);
		btnReset.setText("del");

		table = new TableViewer(area, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		TableViewerColumn col1 = createTableViewerColumn("Action", 100);
		col1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Action a = (Action) element;
				return a.getAction();
			}
		});

		TableViewerColumn col2 = createTableViewerColumn("Operation", 200);
		col2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Action a = (Action) element;
				return a.getOperation();
			}
		});

		TableViewerColumn col3 = createTableViewerColumn("Partner", 130);
		col3.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Action a = (Action) element;
				return a.getPartnerLink();
			}
		});

		try {
			listActions = transformator.getActions();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		table.setContentProvider(new ArrayContentProvider());
		table.setInput(listActions);

		table_1 = table.getTable();
		table_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				int index = table_1.getSelectionIndex();
				append_to_expression(listActions.get(index).getOperation());
			}
		});
		table_1.setBounds(130, 50, 450, 151);
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);

		return area;
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(table, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void append_to_expression(String string) {

		this.expression.add(string);
		this.txtExpression.setText(this.getExpression());

	}

	@Override
	protected void okPressed() {
		this.expression_str = this.getExpression();
		super.okPressed();
	}
}