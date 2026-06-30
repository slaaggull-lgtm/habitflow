(function () {
    'use strict';

    /* ── Toast notifications from flash messages ── */
    function showToast(message, type) {
        var container = document.getElementById('toastContainer');
        if (!container || !message) return;

        var toast = document.createElement('div');
        toast.className = 'toast toast-' + (type || 'success');
        toast.innerHTML = '<span class="toast-icon">' + (type === 'error' ? '✕' : '✓') + '</span><span>' + escapeHtml(message) + '</span>';
        container.appendChild(toast);

        setTimeout(function () {
            toast.remove();
        }, 4000);
    }

    function escapeHtml(text) {
        var div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function initFlashMessages() {
        var success = document.getElementById('flashSuccess');
        var error = document.getElementById('flashError');
        if (success && success.dataset.message) showToast(success.dataset.message, 'success');
        if (error && error.dataset.message) showToast(error.dataset.message, 'error');
    }

    /* ── Confirm delete dialog ── */
    var pendingForm = null;

    function initConfirmDelete() {
        document.querySelectorAll('form[data-confirm-delete]').forEach(function (form) {
            form.addEventListener('submit', function (e) {
                var confirmEnabled = localStorage.getItem('hf_confirmDelete');
                if (confirmEnabled === 'false') return;

                e.preventDefault();
                pendingForm = form;
                var name = form.dataset.habitName || 'this habit';
                document.getElementById('confirmModalText').textContent =
                    'Are you sure you want to delete "' + name + '"? This action cannot be undone.';
                document.getElementById('confirmModal').classList.remove('hidden');
            });
        });

        var cancelBtn = document.getElementById('confirmModalCancel');
        var confirmBtn = document.getElementById('confirmModalConfirm');

        if (cancelBtn) {
            cancelBtn.addEventListener('click', function () {
                document.getElementById('confirmModal').classList.add('hidden');
                pendingForm = null;
            });
        }

        if (confirmBtn) {
            confirmBtn.addEventListener('click', function () {
                if (pendingForm) pendingForm.submit();
                document.getElementById('confirmModal').classList.add('hidden');
                pendingForm = null;
            });
        }

        var overlay = document.getElementById('confirmModal');
        if (overlay) {
            overlay.addEventListener('click', function (e) {
                if (e.target === overlay) {
                    overlay.classList.add('hidden');
                    pendingForm = null;
                }
            });
        }
    }

    /* ── Color picker (habit form) ── */
    window.selectColor = function (hex, el) {
        var input = document.getElementById('colorInput');
        if (input) input.value = hex;
        document.querySelectorAll('.color-dot').forEach(function (d) {
            d.classList.remove('selected');
        });
        if (el) el.classList.add('selected');
    };

    /* ── Avatar picker (profile) ── */
    window.toggleAvatarPicker = function () {
        var panel = document.getElementById('avatarPickerPanel');
        if (panel) panel.style.display = panel.style.display === 'none' ? 'block' : 'none';
    };

    window.pickAvatar = function (emoji) {
        var display = document.getElementById('avatarDisplay');
        if (display) display.textContent = emoji;
        var panel = document.getElementById('avatarPickerPanel');
        if (panel) panel.style.display = 'none';
        localStorage.setItem('hf_avatar', emoji);
    };

    /* ── Settings ── */
    window.setSetting = function (key, val) {
        localStorage.setItem('hf_' + key, val);
        if (key === 'compact') document.body.classList.toggle('compact', val);
        if (key === 'confirmDelete') localStorage.setItem('hf_confirmDelete', val);
    };

    function initSettings() {
        if (localStorage.getItem('hf_compact') === 'true') {
            var compactToggle = document.getElementById('compactToggle');
            if (compactToggle) compactToggle.checked = true;
            document.body.classList.add('compact');
        }
        var confirmDelete = document.getElementById('confirmDelete');
        if (confirmDelete) {
            var saved = localStorage.getItem('hf_confirmDelete');
            confirmDelete.checked = saved !== 'false';
        }
    }

    function initAvatar() {
        var saved = localStorage.getItem('hf_avatar');
        if (saved) {
            var el = document.getElementById('avatarDisplay');
            if (el) el.textContent = saved;
        }
        document.querySelectorAll('.avatar-opt').forEach(function (btn) {
            btn.addEventListener('click', function () {
                pickAvatar(btn.dataset.avatar);
            });
        });
    }

    function initColorPicker() {
        document.querySelectorAll('.color-dot').forEach(function (dot) {
            dot.addEventListener('click', function () {
                selectColor(dot.dataset.color, dot);
            });
        });
    }

    /* ── Mobile menu ── */
    window.toggleMobileMenu = function () {
        var nav = document.getElementById('mobileNav');
        if (nav) nav.classList.toggle('open');
    };

    document.addEventListener('DOMContentLoaded', function () {
        initFlashMessages();
        initConfirmDelete();
        initSettings();
        initAvatar();
        initColorPicker();
    });
})();
