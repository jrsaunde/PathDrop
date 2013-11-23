<script>
$('#cy').cytoscape({
  layout: {
    name: 'circle'
  },
  
  style: cytoscape.stylesheet()
    .selector('node')
      .css({
        'shape': 'data(faveShape)',
        'width': 'mapData(weight, 40, 80, 20, 60)',
        'content': 'data(name)',
        'text-valign': 'center',
        'text-outline-width': 2,
        'text-outline-color': 'data(faveColor)',
        'background-color': 'data(faveColor)',
        'color': '#fff'
      })
    .selector(':selected')
      .css({
        'border-width': 3,
        'border-color': '#333'
      })
    .selector('edge')
      .css({
        'width': 'mapData(strength, 70, 100, 2, 6)',
        'target-arrow-shape': 'triangle',
        'source-arrow-shape': 'triangle',
        'line-color': 'data(faveColor)',
        'source-arrow-color': 'data(faveColor)',
        'target-arrow-color': 'data(faveColor)'
      }),
  
  elements: {
    nodes: [
      { data: { id: 'j', name: 'Jerry', weight: 70, faveColor: '#6FB1FC', faveShape: 'circle' } },
      { data: { id: 'e', name: 'Kramer', weight: 70, faveColor: '#EDA1ED', faveShape: 'circle' } },
      { data: { id: 'k', name: 'Kramer', weight: 70, faveColor: '#86B342', faveShape: 'circle' } },
      { data: { id: 'g', name: 'George', weight: 70, faveColor: '#F5A45D', faveShape: 'circle' } }
    ],
    edges: [
      { data: { source: 'j', target: 'k', faveColor: '#6FB1FC', strength: 80 } },
      { data: { source: 'e', target: 'j', faveColor: '#EDA1ED', strength: 80 }},
      { data: { source: 'k', target: 'e', faveColor: '#86B342', strength: 80 } },
      { data: { source: 'k', target: 'g', faveColor: '#86B342', strength: 80 } },
      { data: { source: 'g', target: 'j', faveColor: '#F5A45D', strength: 80 } }
    ]
  },
  
  ready: function(){
    window.cy = this;
    
    // giddy up
  }
});
</script>