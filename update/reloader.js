function reloadGraph() {
   var now = new Date();
   document.images['graph'].src = 'newQR.png?' + now.getTime()
   timeoutID = setTimeout('reloadGraph()', 10);
}