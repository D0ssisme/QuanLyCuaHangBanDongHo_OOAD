package GUI.Panel.ThongKe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import GUI.Component.PanelBorderRadius;

import BUS.ThongKeBUS;
import config.JDBCUtil;

public final class ThongKe extends JPanel {

    JTabbedPane tabbedPane;
    PanelBorderRadius functionBar;
    JComboBox<String> cbxChiNhanh;
    JButton btnRefresh;
    ThongKeNhanVienBanChay nhanVienBanChay;
    ThongKeDoanhThuChiNhanh doanhThuChiNhanh;
    ThongKeTopSanPham topSanPham;
    ThongKeNhanVienTotNhat nhanVienTotNhat;
    Color BackgroundColor = new Color(248, 249, 250);
    ThongKeBUS thongkeBUS = new ThongKeBUS();

    public ThongKe() {
        initComponent();
    }

    public void initComponent() {
        this.setLayout(new BorderLayout(0, 10));
        this.setBackground(BackgroundColor);

        functionBar = new PanelBorderRadius();
        functionBar.setPreferredSize(new Dimension(0, 70));
        functionBar.setLayout(new BorderLayout(10, 0));
        functionBar.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblChiNhanh = new JLabel("Chi nhánh");
        lblChiNhanh.setBorder(new EmptyBorder(0, 0, 0, 8));
        functionBar.add(lblChiNhanh, BorderLayout.WEST);

        JPanel branchPanel = new JPanel(new BorderLayout());
        branchPanel.setBackground(Color.white);
        branchPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        cbxChiNhanh = new JComboBox<>(new String[]{"Tất cả chi nhánh", "Chi nhánh 1", "Chi nhánh 2", "Chi nhánh 3"});
        cbxChiNhanh.setPreferredSize(new Dimension(220, 35));
        branchPanel.add(cbxChiNhanh, BorderLayout.CENTER);
        functionBar.add(branchPanel, BorderLayout.CENTER);

        btnRefresh = new JButton("Làm mới");
        btnRefresh.setPreferredSize(new Dimension(110, 35));
        btnRefresh.setMinimumSize(new Dimension(110, 35));
        btnRefresh.setMaximumSize(new Dimension(110, 35));
        btnRefresh.setMargin(new Insets(0, 12, 0, 12));
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRefreshClicked();
            }
        });

        JPanel refreshPanel = new JPanel(new BorderLayout());
        refreshPanel.setBackground(functionBar.getBackground());
        refreshPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        refreshPanel.add(btnRefresh, BorderLayout.CENTER);
        functionBar.add(refreshPanel, BorderLayout.EAST);

        this.add(functionBar, BorderLayout.NORTH);

        nhanVienBanChay = new ThongKeNhanVienBanChay(thongkeBUS);
        doanhThuChiNhanh = new ThongKeDoanhThuChiNhanh(thongkeBUS);
        topSanPham = new ThongKeTopSanPham(thongkeBUS);
        nhanVienTotNhat = new ThongKeNhanVienTotNhat(thongkeBUS);

        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.addTab("Nhân viên bán chạy", nhanVienBanChay);
        tabbedPane.addTab("Doanh thu", doanhThuChiNhanh);
        tabbedPane.addTab("Top sản phẩm", topSanPham);
        tabbedPane.addTab("Nhân viên tốt nhất", nhanVienTotNhat);

        this.add(tabbedPane, BorderLayout.CENTER);

        String currentBranchLabel = resolveCurrentBranchLabel();
        cbxChiNhanh.setSelectedItem(currentBranchLabel);
        cbxChiNhanh.addActionListener(e -> onBranchChanged());
        onBranchChanged();
    }

    private void onBranchChanged() {
        String selectedBranch = (String) cbxChiNhanh.getSelectedItem();
        nhanVienBanChay.refreshData(selectedBranch);
        doanhThuChiNhanh.refreshData(selectedBranch);
        topSanPham.refreshData(selectedBranch);
        nhanVienTotNhat.refreshData(selectedBranch);
    }

    private void onRefreshClicked() {
        onBranchChanged();
    }

    private String resolveCurrentBranchLabel() {
        String currentMcn = JDBCUtil.getCurrentMcn();
        if (currentMcn == null || currentMcn.isBlank()) {
            return "Tất cả chi nhánh";
        }
        switch (currentMcn.trim().toUpperCase()) {
            case "CN1":
                return "Chi nhánh 1";
            case "CN2":
                return "Chi nhánh 2";
            case "CN3":
                return "Chi nhánh 3";
            default:
                return "Tất cả chi nhánh";
        }
    }
}
