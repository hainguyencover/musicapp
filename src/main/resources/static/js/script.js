// Lắng nghe sự kiện khi modal sắp được hiển thị
const artistDetailModal = document.getElementById('artistDetailModal');
artistDetailModal.addEventListener('show.bs.modal', event => {
    // Nút đã kích hoạt modal
    const button = event.relatedTarget;

    // Trích xuất thông tin từ data-* attributes
    const id = button.getAttribute('data-artist-id');
    const name = button.getAttribute('data-artist-name');
    const bio = button.getAttribute('data-artist-bio');
    const avatarPath = button.getAttribute('data-artist-avatar-path');
    const avatarExists = button.getAttribute('data-artist-avatar-exists') === 'true';

    // Lấy các element trong modal
    const modalId = artistDetailModal.querySelector('#modalArtistId');
    const modalName = artistDetailModal.querySelector('#modalArtistName');
    const modalBio = artistDetailModal.querySelector('#modalArtistBio');
    const modalAvatar = artistDetailModal.querySelector('#modalArtistAvatar');
    const modalAvatarNA = artistDetailModal.querySelector('#modalArtistAvatarNA');

    // Cập nhật nội dung cho modal
    modalId.textContent = id;
    modalName.textContent = name;

    // Xử lý tiểu sử (nếu rỗng)
    if (bio && bio.trim() !== 'null' && bio.trim() !== '') {
        modalBio.textContent = bio;
    } else {
        modalBio.textContent = 'Không có tiểu sử.';
    }

    // Xử lý ảnh (hiển thị ảnh hoặc text N/A)
    if (avatarExists) {
        modalAvatar.src = avatarPath;
        modalAvatar.style.display = 'block';
        modalAvatarNA.style.display = 'none';
    } else {
        modalAvatar.src = '';
        modalAvatar.style.display = 'none';
        modalAvatarNA.style.display = 'block';
    }
});
