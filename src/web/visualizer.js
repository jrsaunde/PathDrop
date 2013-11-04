$('#cy').cytoscape({
  
  style: cytoscape.stylesheet()
    .selector('node')
      .css({
        'content': 'data(name)',
        'text-valign': 'center',
        'color': 'white',
        'text-outline-width': 2,
        'text-outline-color': '#888'
      })
    .selector('edge')
      .css({
        'target-arrow-shape': 'triangle'
      })
    .selector(':selected')
      .css({
        'background-color': 'black',
        'line-color': 'black',
        'target-arrow-color': 'black',
        'source-arrow-color': 'black'
      })
    .selector('.faded')
      .css({
        'opacity': 0.25,
        'text-opacity': 0
      })
    .selector('edge').css({ content: 'data(label)' }),
  
  elements: {
     nodes: [
{ data: { id: 'a', name: 'R1' } },
{ data: { id: 'b', name: 'R2' } },
{ data: { id: 'c', name: 'R3' } },
{ data: { id: 'd', name: 'R4' } },
{ data: { id: 'e', name: 'R5' } },
{ data: { id: 'f', name: 'R6' } }
],
edges: [
{ data: { source: 'a', target: 'b', label:'Gi0/1 Gi0/0'} },
{ data: { source: 'a', target: 'c', label:'Gi0/2 Gi0/0' } },
{ data: { source: 'b', target: 'c', label:'Gi0/1 Gi0/1' } },
{ data: { source: 'b', target: 'd', label:'Gi0/2 Gi0/0' } },
{ data: { source: 'b', target: 'e', label:'Gi0/3 Gi0/0' } },
{ data: { source: 'c', target: 'e', label:'Gi0/2 Gi0/1' } },
{ data: { source: 'c', target: 'f', label:'Gi0/3 Gi0/0' } },
{ data: { source: 'd', target: 'e', label:'Gi0/1 Gi0/2' } },
{ data: { source: 'e', target: 'f', label:'Gi0/3 Gi0/1' } }
]
},
  
  ready: function(){
    window.cy = this;
   
    cy.elements().unselectify();
    
    cy.on('tap', 'node', function(e){
      var node = e.cyTarget; 
      var neighborhood = node.neighborhood().add(node);
      
      cy.elements().addClass('faded');
      neighborhood.removeClass('faded');
    });
    
    cy.on('tap', function(e){
      if( e.cyTarget === cy ){
        cy.elements().removeClass('faded');
      }
    });
  }
});