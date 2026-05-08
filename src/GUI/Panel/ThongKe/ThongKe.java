package GUI.Panel.ThongKe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import GUI.Component.PanelBorderRadius;

import BUS.ThongKeBUS;

public final class ThongKe extends JPanel {

    JTabbedPane tabbedPane;
    PanelBorderRadius functionBar;
    JComboBox<String> cbxChiNhanh;
    JPanel tongquan, nhacungcap, khachhang, doanhthu;
    ThongKeTonKho nhapxuat;
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

        this.add(functionBar, BorderLayout.NORTH);

        tongquan = new ThongKeTongQuan(thongkeBUS);
        nhapxuat = new ThongKeTonKho(thongkeBUS);
        khachhang = new ThongKeKhachHang(thongkeBUS);
        nhacungcap = new ThongKeNhaCungCap(thongkeBUS);
        doanhthu = new ThongKeDoanhThu(thongkeBUS);

        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.addTab("Tổng quan", tongquan);
        tabbedPane.addTab("Tồn kho", nhapxuat);
        tabbedPane.addTab("Doanh thu", doanhthu);
        tabbedPane.addTab("Nhà cung cấp", nhacungcap);
        tabbedPane.addTab("Khách hàng", khachhang);

        this.add(tabbedPane, BorderLayout.CENTER);
    }
}
