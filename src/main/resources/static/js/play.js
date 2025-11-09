/**
 * Music Player Script for play.html
 * Handles all player functionality including play/pause, seek, volume control
 */

document.addEventListener("DOMContentLoaded", () => {
    // Get all DOM elements
    const audioPlayer = document.getElementById("audioPlayer");
    const playerDisc = document.getElementById("playerDisc");
    const playPauseBtn = document.getElementById("btnPlayPause");
    const iconPlay = document.getElementById("iconPlay");
    const iconPause = document.getElementById("iconPause");
    const seekBar = document.getElementById("seekBar");
    const currentTimeEl = document.getElementById("currentTime");
    const durationEl = document.getElementById("duration");
    const volumeBar = document.getElementById("volumeBar");

    // ===== PHASE 1: CORE PLAYER FUNCTIONALITY =====

    /**
     * Play/Pause button handler
     */
    playPauseBtn.addEventListener("click", () => {
        if (audioPlayer.paused) {
            audioPlayer.play();
            playerDisc.classList.add("playing"); // Start disc rotation
            iconPlay.style.display = "none";
            iconPause.style.display = "block";
        } else {
            audioPlayer.pause();
            playerDisc.classList.remove("playing"); // Stop disc rotation
            iconPlay.style.display = "block";
            iconPause.style.display = "none";
        }
    });

    /**
     * Update seek bar and current time as music plays
     */
    audioPlayer.addEventListener("timeupdate", () => {
        // Update seek bar position
        const progress = (audioPlayer.currentTime / audioPlayer.duration) * 100;
        seekBar.value = progress || 0;

        // Update current time display
        currentTimeEl.textContent = formatTime(audioPlayer.currentTime);
    });

    /**
     * Seek functionality - jump to specific time
     */
    seekBar.addEventListener("input", () => {
        const time = (seekBar.value / 100) * audioPlayer.duration;
        audioPlayer.currentTime = time;
    });

    /**
     * Volume control
     */
    volumeBar.addEventListener("input", () => {
        audioPlayer.volume = volumeBar.value;
    });

    /**
     * Load audio metadata and set duration
     */
    audioPlayer.addEventListener("loadedmetadata", () => {
        durationEl.textContent = formatTime(audioPlayer.duration);
        seekBar.max = 100; // Ensure seek bar max is 100%
    });

    /**
     * Handle when song ends
     */
    audioPlayer.addEventListener("ended", () => {
        playerDisc.classList.remove("playing");
        iconPlay.style.display = "block";
        iconPause.style.display = "none";
        seekBar.value = 0;
        currentTimeEl.textContent = "0:00";
    });

    /**
     * Format time from seconds to MM:SS format
     * @param {number} seconds - Time in seconds
     * @returns {string} Formatted time string
     */
    function formatTime(seconds) {
        if (isNaN(seconds)) return "0:00";

        const minutes = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${minutes}:${secs < 10 ? '0' : ''}${secs}`;
    }

    // ===== PHASE 2: ADDITIONAL CONTROLS (STUBS) =====

    /**
     * Shuffle button (placeholder)
     */
    document.getElementById("btnShuffle").onclick = () => {
        alert("Chức năng Trộn bài sẽ được cập nhật trong phiên bản sau!");
    };

    /**
     * Previous button (placeholder)
     */
    document.getElementById("btnBack").onclick = () => {
        alert("Chức năng Bài trước sẽ được cập nhật trong phiên bản sau!");
    };

    /**
     * Next button (placeholder)
     */
    document.getElementById("btnNext").onclick = () => {
        alert("Chức năng Bài tiếp theo sẽ được cập nhật trong phiên bản sau!");
    };

    /**
     * Repeat button (placeholder)
     */
    document.getElementById("btnRepeat").onclick = () => {
        alert("Chức năng Lặp lại sẽ được cập nhật trong phiên bản sau!");
    };

    /**
     * Like button (placeholder)
     */
    document.getElementById("btnLike").onclick = () => {
        alert("Chức năng Thích bài hát sẽ được cập nhật trong phiên bản sau!");
    };

    // ===== KEYBOARD SHORTCUTS =====

    /**
     * Add keyboard shortcuts for better UX
     */
    document.addEventListener("keydown", (e) => {
        // Spacebar: Play/Pause
        if (e.code === "Space" && e.target.tagName !== "INPUT") {
            e.preventDefault();
            playPauseBtn.click();
        }

        // Arrow Right: Forward 5 seconds
        if (e.code === "ArrowRight") {
            e.preventDefault();
            audioPlayer.currentTime = Math.min(
                audioPlayer.currentTime + 5,
                audioPlayer.duration
            );
        }

        // Arrow Left: Backward 5 seconds
        if (e.code === "ArrowLeft") {
            e.preventDefault();
            audioPlayer.currentTime = Math.max(audioPlayer.currentTime - 5, 0);
        }

        // Arrow Up: Increase volume
        if (e.code === "ArrowUp") {
            e.preventDefault();
            volumeBar.value = Math.min(parseFloat(volumeBar.value) + 0.1, 1);
            audioPlayer.volume = volumeBar.value;
        }

        // Arrow Down: Decrease volume
        if (e.code === "ArrowDown") {
            e.preventDefault();
            volumeBar.value = Math.max(parseFloat(volumeBar.value) - 0.1, 0);
            audioPlayer.volume = volumeBar.value;
        }
    });

    // ===== ERROR HANDLING =====

    /**
     * Handle audio loading errors
     */
    audioPlayer.addEventListener("error", (e) => {
        console.error("Audio loading error:", e);
        alert("Lỗi: Không thể tải bài hát. Vui lòng thử lại sau.");
    });

    // ===== INITIALIZATION =====

    /**
     * Initialize player state
     */
    function initPlayer() {
        // Set initial volume
        audioPlayer.volume = volumeBar.value;

        // Reset play/pause icons
        iconPlay.style.display = "block";
        iconPause.style.display = "none";

        // Reset seek bar
        seekBar.value = 0;

        console.log("Music player initialized successfully");
    }

    // Initialize the player
    initPlayer();
});
