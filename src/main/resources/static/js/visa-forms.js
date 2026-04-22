(function () {
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

  if (typeSelect) {
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
})();
