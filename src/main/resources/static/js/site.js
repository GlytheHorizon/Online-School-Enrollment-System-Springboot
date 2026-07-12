/* ── Site-wide Enhancements ── */

(function() {
  'use strict';

  // ── Toast System ──
  window.Toast = {
    container: null,
    init() {
      if (!this.container) {
        this.container = document.createElement('div');
        this.container.className = 'toast-container';
        document.body.appendChild(this.container);
      }
    },
    show(type, title, message, duration) {
      this.init();
      duration = duration || 4000;
      const icons = {
        success: 'fa-check-circle',
        error: 'fa-exclamation-circle',
        warning: 'fa-exclamation-triangle',
        info: 'fa-info-circle'
      };
      const el = document.createElement('div');
      el.className = 'toast toast-' + type;
      el.innerHTML =
        '<div class="toast-icon"><i class="fas ' + (icons[type] || icons.info) + '"></i></div>' +
        '<div class="toast-body">' +
          '<div class="toast-title">' + this.esc(title) + '</div>' +
          (message ? '<div class="toast-message">' + this.esc(message) + '</div>' : '') +
        '</div>' +
        '<button class="toast-close" onclick="this.parentElement.remove()">&times;</button>';
      this.container.appendChild(el);

      if (duration > 0) {
        setTimeout(function() {
          if (el.parentNode) {
            el.style.animation = 'toastOut 0.3s ease forwards';
            setTimeout(function() { if (el.parentNode) el.remove(); }, 300);
          }
        }, duration);
      }
    },
    success(title, msg, dur) { this.show('success', title, msg, dur); },
    error(title, msg, dur) { this.show('error', title, msg, dur); },
    warning(title, msg, dur) { this.show('warning', title, msg, dur); },
    info(title, msg, dur) { this.show('info', title, msg, dur); },
    esc(s) {
      if (!s) return '';
      var div = document.createElement('div');
      div.appendChild(document.createTextNode(s));
      return div.innerHTML;
    }
  };

  // ── Modal Confirm System ──
  window.Modal = {
    overlay: null,
    init() {
      if (this.overlay) return;
      this.overlay = document.createElement('div');
      this.overlay.className = 'modal-overlay';
      this.overlay.innerHTML =
        '<div class="modal">' +
          '<div class="modal-header">' +
            '<div class="modal-icon info"><i class="fas fa-question-circle"></i></div>' +
            '<div class="modal-header-text">' +
              '<h3 id="modalTitle">Confirm</h3>' +
              '<p id="modalDesc">Are you sure?</p>' +
            '</div>' +
          '</div>' +
          '<div class="modal-body" id="modalBody"></div>' +
          '<div class="modal-footer">' +
            '<button class="btn btn-ghost" id="modalCancelBtn">Cancel</button>' +
            '<button class="btn btn-primary" id="modalConfirmBtn">Confirm</button>' +
          '</div>' +
        '</div>';
      document.body.appendChild(this.overlay);
      var self = this;
      this.overlay.addEventListener('click', function(e) {
        if (e.target === self.overlay) self.close();
      });
      document.getElementById('modalCancelBtn').addEventListener('click', function() { self.close(); });
    },
    confirm(options) {
      var self = this;
      return new Promise(function(resolve) {
        self.init();
        var iconEl = self.overlay.querySelector('.modal-icon');
        var titleEl = document.getElementById('modalTitle');
        var descEl = document.getElementById('modalDesc');
        var bodyEl = document.getElementById('modalBody');
        var confirmBtn = document.getElementById('modalConfirmBtn');
        var cancelBtn = document.getElementById('modalCancelBtn');

        var type = options.type || 'warning';
        var iconMap = {
          danger: { bg: 'danger', ic: 'fa-exclamation-triangle' },
          warning: { bg: 'warning', ic: 'fa-exclamation-triangle' },
          info: { bg: 'info', ic: 'fa-question-circle' }
        };
        var m = iconMap[type] || iconMap.warning;
        iconEl.className = 'modal-icon ' + m.bg;
        iconEl.innerHTML = '<i class="fas ' + m.ic + '"></i>';
        titleEl.textContent = options.title || 'Confirm';
        descEl.textContent = options.message || 'Are you sure?';

        if (options.body) {
          bodyEl.innerHTML = options.body;
          bodyEl.style.display = 'block';
        } else {
          bodyEl.style.display = 'none';
        }

        if (options.confirmText) confirmBtn.textContent = options.confirmText;
        else confirmBtn.innerHTML = '<i class="fas fa-check"></i> Confirm';
        if (options.cancelText) cancelBtn.textContent = options.cancelText;
        else cancelBtn.innerHTML = 'Cancel';

        if (options.confirmClass) {
          confirmBtn.className = 'btn ' + options.confirmClass;
        } else if (type === 'danger') {
          confirmBtn.className = 'btn btn-danger';
        } else {
          confirmBtn.className = 'btn btn-primary';
        }

        self.overlay.classList.add('open');

        function cleanup() {
          self.overlay.classList.remove('open');
          confirmBtn.removeEventListener('click', onConfirm);
          cancelBtn.removeEventListener('click', onCancel);
        }
        function onConfirm() { cleanup(); resolve(true); }
        function onCancel() { cleanup(); resolve(false); }
        confirmBtn.addEventListener('click', onConfirm);
        cancelBtn.addEventListener('click', onCancel);
      });
    },
    close() {
      if (this.overlay) this.overlay.classList.remove('open');
    }
  };

  // ── Sidebar Toggle ──
  function setupSidebar() {
    var sidebar = document.querySelector('.sidebar');
    var mainContent = document.querySelector('.main-content');
    if (!sidebar) return;

    var toggle = document.createElement('button');
    toggle.className = 'sidebar-toggle';
    toggle.innerHTML = '<i class="fas fa-bars"></i>';
    toggle.setAttribute('aria-label', 'Toggle sidebar');
    document.body.appendChild(toggle);

    var overlay = document.createElement('div');
    overlay.className = 'sidebar-overlay';
    document.body.appendChild(overlay);

    function openSidebar() {
      sidebar.classList.add('open');
      overlay.classList.add('open');
      toggle.innerHTML = '<i class="fas fa-times"></i>';
    }
    function closeSidebar() {
      sidebar.classList.remove('open');
      overlay.classList.remove('open');
      toggle.innerHTML = '<i class="fas fa-bars"></i>';
    }
    toggle.addEventListener('click', function() {
      if (sidebar.classList.contains('open')) closeSidebar();
      else openSidebar();
    });
    overlay.addEventListener('click', closeSidebar);

    window.addEventListener('resize', function() {
      if (window.innerWidth > 768 && sidebar.classList.contains('open')) {
        closeSidebar();
      }
    });
  }

  // ── Auto-dismiss alerts ──
  function setupAlerts() {
    document.querySelectorAll('.alert').forEach(function(el) {
      var closeBtn = el.querySelector('.alert-close');
      if (closeBtn) {
        closeBtn.addEventListener('click', function() { el.remove(); });
      }
      setTimeout(function() {
        if (el.parentNode) {
          el.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
          el.style.opacity = '0';
          el.style.transform = 'translateY(-8px)';
          setTimeout(function() { if (el.parentNode) el.remove(); }, 300);
        }
      }, 5000);
    });
  }

  // ── Client-side table filter ──
  function setupTableFilter() {
    document.querySelectorAll('[data-filter]').forEach(function(input) {
      input.addEventListener('input', function() {
        var query = this.value.toLowerCase();
        var tableId = this.getAttribute('data-filter');
        var table = document.getElementById(tableId);
        if (!table) return;
        var rows = table.querySelectorAll('tbody tr');
        rows.forEach(function(row) {
          var text = row.textContent.toLowerCase();
          row.style.display = text.indexOf(query) > -1 ? '' : 'none';
        });
      });
    });
  }

  // ── Replace confirm() calls with modal ──
  function setupConfirmReplacements() {
    document.addEventListener('click', function(e) {
      var target = e.target.closest('[data-confirm]');
      if (!target) return;
      e.preventDefault();

      var msg = target.getAttribute('data-confirm');
      var form = target.closest('form');

      Modal.confirm({
        title: target.getAttribute('data-confirm-title') || 'Confirm Action',
        message: msg,
        type: target.getAttribute('data-confirm-type') || 'warning',
        confirmText: target.getAttribute('data-confirm-text') || 'Proceed',
        confirmClass: target.getAttribute('data-confirm-class') || 'btn-danger'
      }).then(function(confirmed) {
        if (confirmed && form) form.submit();
      });
    });
  }

  // ── Format inputs ──
  function setupInputFormatting() {
    document.querySelectorAll('[data-format="studentId"]').forEach(function(el) {
      el.addEventListener('input', function() {
        var val = this.value.replace(/\D/g, '').slice(0, 8);
        if (val.length > 4) val = val.slice(0, 4) + '-' + val.slice(4);
        this.value = val;
      });
    });
    document.querySelectorAll('[data-format="phone"]').forEach(function(el) {
      el.addEventListener('input', function() {
        this.value = this.value.replace(/\D/g, '').slice(0, 11);
      });
    });
    document.querySelectorAll('[data-format="integer"]').forEach(function(el) {
      el.addEventListener('input', function() {
        this.value = this.value.replace(/\D/g, '');
      });
    });
    document.querySelectorAll('[data-format="decimal"]').forEach(function(el) {
      el.addEventListener('input', function() {
        var val = this.value.replace(/[^\d.]/g, '');
        var parts = val.split('.');
        if (parts.length > 2) val = parts[0] + '.' + parts.slice(1).join('');
        if (parts.length === 2 && parts[1].length > 2) val = parts[0] + '.' + parts[1].slice(0, 2);
        this.value = val;
      });
    });
  }

  // ── Init ──
  document.addEventListener('DOMContentLoaded', function() {
    setupSidebar();
    setupAlerts();
    setupTableFilter();
    setupConfirmReplacements();
    setupInputFormatting();
  });
})();
