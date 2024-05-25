/**
 * Creates a table row (tr) with the specified column values.
 * @param columns The columns that should be added.
 * @returns {HTMLTableRowElement} The table row.
 */
function createRow(columns) {
    let row = document.createElement("tr");

    for (const column of columns) {
        let packageCell = document.createElement("td");

        packageCell.innerText = column;

        row.appendChild(packageCell);
    }

    return row;
}