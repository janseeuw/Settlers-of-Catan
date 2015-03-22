/**
 * Sets the language for the website.
 * @param the language
 */
function setLanguage(language) {
  document.getElementById("languageForm").language.value = language;
  document.getElementById("languageForm").submit();
}