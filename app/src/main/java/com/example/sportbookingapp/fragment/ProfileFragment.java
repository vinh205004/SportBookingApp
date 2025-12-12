package com.example.sportbookingapp.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.database.UserDAO;
import com.example.sportbookingapp.model.User;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvPhone, tvEmail;
    private ImageView btnBack, btnEditName, btnEditPhone, btnEditEmail;

    // 2 biến để quản lý dữ liệu
    private UserDAO userDAO;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);

        // 1. Khởi tạo DAO và load dữ liệu
        userDAO = new UserDAO(requireContext());

        // Tạo user mặc định nếu chưa có
        userDAO.createDefaultUserIfNotExist();

        // Load User ID = 1 lên giao diện
        loadUserData();

        setupEvents();
        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBackBooking);
        tvName = view.findViewById(R.id.tv_name);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvEmail = view.findViewById(R.id.tv_email);
        btnEditName = view.findViewById(R.id.btn_edit_name);
        btnEditPhone = view.findViewById(R.id.btn_edit_phone);
        btnEditEmail = view.findViewById(R.id.btn_edit_email);
    }

    private void loadUserData() {
        // Lấy user số 1 từ DB
        currentUser = userDAO.getUserById(1);

        if (currentUser != null) {
            tvName.setText(currentUser.getFullName());
            tvPhone.setText(currentUser.getPhone());
            tvEmail.setText(currentUser.getEmail());
        }
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        // Khi bấm nút sửa, truyền thêm loại thông tin cần sửa (1: Tên, 2: SĐT, 3: Email)
        btnEditName.setOnClickListener(v -> showEditDialog("Sửa tên", 1));
        btnEditPhone.setOnClickListener(v -> showEditDialog("Sửa số điện thoại", 2));
        btnEditEmail.setOnClickListener(v -> showEditDialog("Sửa email", 3));
    }

    // Hàm Dialog sửa đổi chút để xử lý lưu vào DB
    private void showEditDialog(String title, int fieldType) {
        EditText editText = new EditText(requireContext());
        editText.setPadding(40, 40, 40, 40);

        // Hiển thị text hiện tại vào ô nhập
        if (currentUser != null) {
            if (fieldType == 1) editText.setText(currentUser.getFullName());
            else if (fieldType == 2) editText.setText(currentUser.getPhone());
            else if (fieldType == 3) editText.setText(currentUser.getEmail());
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setView(editText)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newValue = editText.getText().toString().trim();
                    if (!newValue.isEmpty() && currentUser != null) {

                        // 2. Cập nhật vào biến tạm currentUser
                        if (fieldType == 1) currentUser.setFullName(newValue);
                        else if (fieldType == 2) currentUser.setPhone(newValue);
                        else if (fieldType == 3) currentUser.setEmail(newValue);

                        // 3. Ghi đè vào Database (QUAN TRỌNG)
                        boolean success = userDAO.updateUser(currentUser);

                        if (success) {
                            Toast.makeText(getContext(), "Đã cập nhật!", Toast.LENGTH_SHORT).show();
                            loadUserData(); // Load lại giao diện từ biến mới
                        } else {
                            Toast.makeText(getContext(), "Lỗi lưu database!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}