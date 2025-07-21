document.addEventListener('DOMContentLoaded', () => {
  const input   = document.getElementById('searchBooks');
  const select  = document.getElementById('books');
  // "clono" l'array di option per tenere il testo originale
  const options = Array.from(select.options).map(opt => ({
    element: opt,
    text: opt.text.toLowerCase()
  }));

  input.addEventListener('input', () => {
    const filtro = input.value.trim().toLowerCase();
    options.forEach(({ element, text }) => {
      // se matcho mostro, altrimenti nascondo
      element.style.display = text.includes(filtro) ? '' : 'none';
    });
  });
});
