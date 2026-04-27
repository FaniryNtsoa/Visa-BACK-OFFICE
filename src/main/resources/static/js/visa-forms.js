(function () {
  function initVisaSections() {
    const typeSelect = document.querySelector("[data-type-visa]");
    const investSection = document.querySelector("[data-section='investisseur']");
    const travailSection = document.querySelector("[data-section='travailleur']");

    function toggleSections(category) {
      if (investSection) {
        investSection.style.display = category === "investisseur" ? "block" : "none";
      }
      if (travailSection) {
        travailSection.style.display = category === "travailleur" ? "block" : "none";
      }
    }

    function applyObligatoires(typeId) {
      if (!window.obligatoiresByType) {
        return;
      }

      const keys = window.obligatoiresByType[typeId] || [];
      const inputs = document.querySelectorAll("[data-field-key]");

      inputs.forEach((input) => {
        const key = input.getAttribute("data-field-key");
        const isRequired = keys.includes(key);
        input.required = isRequired;

        const label = document.querySelector("label[for='" + input.id + "']");
        if (label) {
          label.classList.toggle("is-required", isRequired);
        }
      });
    }

    if (!typeSelect) {
      return;
    }

    typeSelect.addEventListener("change", () => {
      const option = typeSelect.options[typeSelect.selectedIndex];
      const category = option ? option.getAttribute("data-category") : "";
      toggleSections(category);
      applyObligatoires(option ? option.value : null);
    });

    const option = typeSelect.options[typeSelect.selectedIndex];
    toggleSections(option ? option.getAttribute("data-category") : "");
    applyObligatoires(option ? option.value : null);
  }

  function initAppShell() {
    const body = document.body;
    const header = document.querySelector("body > header");
    const main = document.querySelector("body > main");
    if (!header || !main || body.dataset.appShellReady === "1") {
      return;
    }

    const navItems = [
      { href: "/", label: "Accueil", icon: "HM" },
      { href: "/demandes/liste", label: "Dossiers", icon: "DS" },
      { href: "/demandes/nouvelle", label: "Nouvelle", icon: "NV" },
      { href: "/demandes/transfert", label: "Transfert", icon: "TR" },
      { href: "/demandes/duplicata", label: "Duplicata", icon: "DP" }
    ];

    const shell = document.createElement("div");
    shell.className = "app-shell";

    const sidebar = document.createElement("aside");
    sidebar.className = "app-sidebar";
    sidebar.innerHTML =
      '<div class="app-sidebar-bg" aria-hidden="true"></div>' +
      '<div class="app-sidebar-brand">' +
      '<div class="app-brand-line">' +
      '<span class="app-brand-mark" aria-hidden="true"></span>' +
      '<span>Visa Back Office</span>' +
      "</div>" +
      '<p class="app-sidebar-subtitle">Administration</p>' +
      "</div>" +
      '<nav class="app-sidebar-nav" aria-label="Navigation principale"></nav>' +
      '<div class="app-sidebar-footer">' +
      '<div class="app-profile-card">' +
      '<h4>Espace securise</h4>' +
      '<p>Traitement des dossiers visa</p>' +
      "</div>" +
      '<div class="app-sidebar-controls"></div>' +
      "</div>";

    const nav = sidebar.querySelector(".app-sidebar-nav");
    const currentPath = window.location.pathname;
    navItems.forEach((item) => {
      const a = document.createElement("a");
      a.href = item.href;
      a.className = "app-sidebar-link";
      const isRoot = item.href === "/";
      const isActive = isRoot ? currentPath === "/" : currentPath.startsWith(item.href);
      if (isActive) {
        a.classList.add("active");
      }

      a.innerHTML =
        '<span class="app-sidebar-link-icon" aria-hidden="true">' +
        item.icon +
        "</span>" +
        '<span class="app-sidebar-link-text">' +
        item.label +
        "</span>";
      nav.appendChild(a);
    });

    const controls = sidebar.querySelector(".app-sidebar-controls");
    const collapseButton = document.createElement("button");
    collapseButton.type = "button";
    collapseButton.className = "app-sidebar-toggle";
    collapseButton.innerHTML = '<span>Reduire</span>';
    controls.appendChild(collapseButton);

    const content = document.createElement("div");
    content.className = "app-content";

    const topbar = document.createElement("div");
    topbar.className = "app-topbar";
    const menuButton = document.createElement("button");
    menuButton.type = "button";
    menuButton.className = "app-menu-toggle";
    menuButton.setAttribute("aria-label", "Ouvrir le menu");
    menuButton.textContent = "Menu";
    const topbarTitle = document.createElement("h2");
    topbarTitle.textContent = header.textContent.trim();
    topbar.appendChild(menuButton);
    topbar.appendChild(topbarTitle);

    header.parentNode.insertBefore(shell, header);
    content.appendChild(topbar);
    content.appendChild(header);
    content.appendChild(main);
    shell.appendChild(sidebar);
    shell.appendChild(content);

    const savedState = window.localStorage.getItem("bo-sidebar-collapsed");
    if (savedState === "1") {
      shell.classList.add("sidebar-collapsed");
    }

    collapseButton.addEventListener("click", () => {
      shell.classList.toggle("sidebar-collapsed");
      const collapsed = shell.classList.contains("sidebar-collapsed") ? "1" : "0";
      window.localStorage.setItem("bo-sidebar-collapsed", collapsed);
    });

    menuButton.addEventListener("click", () => {
      shell.classList.toggle("menu-open");
    });

    content.addEventListener("click", () => {
      if (shell.classList.contains("menu-open")) {
        shell.classList.remove("menu-open");
      }
    });

    sidebar.addEventListener("click", (event) => {
      event.stopPropagation();
    });

    body.dataset.appShellReady = "1";
  }

  function initModalForms() {
    const forms = document.querySelectorAll("form.modal-form");
    if (!forms.length) {
      return;
    }

    forms.forEach((form, index) => {
      if (form.dataset.modalReady === "1") {
        return;
      }

      const card = form.closest(".card") || form.parentElement;
      const modalId = "app-modal-" + index;
      const title = form.getAttribute("data-modal-title") || "Formulaire";
      const triggerLabel = form.getAttribute("data-modal-trigger") || "Ouvrir le formulaire";

      const trigger = document.createElement("button");
      trigger.type = "button";
      trigger.className = "modal-open-btn";
      trigger.textContent = triggerLabel;
      trigger.setAttribute("aria-controls", modalId);

      const context = document.createElement("section");
      context.className = "modal-page-context";
      context.innerHTML =
        "<h3>" + title + "</h3>" +
        "<p>Utilisez le formulaire modal pour saisir les informations de maniere guidee, avec une meilleure lisibilite.</p>" +
        '<div class="modal-context-grid">' +
        '<div class="modal-context-box"><strong>Flux simplifie</strong><span>Saisissez, verifiez puis validez depuis une seule fenetre.</span></div>' +
        '<div class="modal-context-box"><strong>Qualite des donnees</strong><span>Les champs requis sont indiques automatiquement selon le type de dossier.</span></div>' +
        "</div>";

      card.insertBefore(context, form);
      card.insertBefore(trigger, form);

      const overlay = document.createElement("div");
      overlay.className = "app-modal-overlay";
      overlay.id = modalId;
      overlay.setAttribute("aria-hidden", "true");

      const modal = document.createElement("div");
      modal.className = "app-modal";
      modal.setAttribute("role", "dialog");
      modal.setAttribute("aria-modal", "true");
      modal.setAttribute("aria-label", title);

      const head = document.createElement("div");
      head.className = "app-modal-head";
      const heading = document.createElement("h2");
      heading.textContent = title;
      const close = document.createElement("button");
      close.type = "button";
      close.className = "app-modal-close";
      close.textContent = "X";
      close.setAttribute("aria-label", "Fermer");
      head.appendChild(heading);
      head.appendChild(close);

      const body = document.createElement("div");
      body.className = "app-modal-body";
      form.classList.add("app-modal-form");
      body.appendChild(form);

      modal.appendChild(head);
      modal.appendChild(body);
      overlay.appendChild(modal);
      document.body.appendChild(overlay);

      const openModal = () => {
        overlay.classList.add("open");
        overlay.setAttribute("aria-hidden", "false");
      };

      const closeModal = () => {
        overlay.classList.remove("open");
        overlay.setAttribute("aria-hidden", "true");
      };

      trigger.addEventListener("click", openModal);
      close.addEventListener("click", closeModal);

      overlay.addEventListener("click", (event) => {
        if (event.target === overlay) {
          closeModal();
        }
      });

      document.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
          closeModal();
        }
      });

      if (document.querySelector(".notice.error")) {
        openModal();
      }

      form.dataset.modalReady = "1";
    });
  }

  document.addEventListener("DOMContentLoaded", () => {
    initAppShell();
    initModalForms();
    initVisaSections();
  });
})();
