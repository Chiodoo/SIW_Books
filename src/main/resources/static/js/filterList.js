document.addEventListener('DOMContentLoaded', () => {
  // Trova tutti gli input di ricerca con la classe .form-filter
  document.querySelectorAll('input.form-filter').forEach(input => {
    const targetId = input.dataset.target;            // es. "books" o "authors"
    const select   = document.getElementById(targetId);
    if (!select) return;

    // Clona i testi originali per non rompere l'option
    const options = Array.from(select.options).map(opt => ({
      element: opt,
      text: opt.text.toLowerCase()
    }));

    input.addEventListener('input', () => {
      const filtro = input.value.trim().toLowerCase();
      options.forEach(({ element, text }) => {
        element.style.display = (!filtro || text.includes(filtro)) ? '' : 'none';
      });
    });
  });
});
