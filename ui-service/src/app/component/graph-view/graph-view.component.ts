import {AfterContentInit, AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {CgData} from '../../model/cg-data';
import * as d3 from 'd3';
import {GRAPHDATA} from '../../model/mock-data';
import {CgTriple} from '../../model/cg-triple';
import {CgNodeItem} from '../../model/cg-node-item';
import {CgLineItem} from '../../model/cg-line-item';
import {CgPath} from '../../model/cg-path';
import {NgControlStatusGroup} from '@angular/forms';
import {UniqueIdProviderService} from '../../service/unique-id-provider.service';
import {EventProviderService} from '../../service/event/event-provider.service';
import {style} from 'd3';

@Component({
  selector: 'app-graph-view',
  templateUrl: './graph-view.component.html',
  styleUrls: ['./graph-view.component.css']
})
export class GraphViewComponent implements OnInit, AfterViewInit {
  @Input()
  graphData: CgData;
  g;
  svg;
  linesG;

  private minXDist = 800;
  private minYDist = 120;
  private nodeRad = 40;
  private pathStrFac = 3;
  private sNode: CgNodeItem;
  private eNode: CgNodeItem;
  private sNodeX: number;
  private sNodeY: number;
  private maxXDist: number;
  private tooltipDiv;
  private ttSpan;
  private nodeArr: CgNodeItem[] = [];
  private edgeArr: CgLineItem[] = [];
  private mouseOutTog = -2;
  myRegexp = /\/([^\/]+)$/g;

  static getUriName(uri: string) {
    const myRegexp = /\/([^\/]+)$/g;
    const match = myRegexp.exec(uri);
    let prefix = '';
    if (uri.match(/dbpedia\.org\/ontology/g)) {
      prefix = 'dbo:';
    } else if (uri.match(/dbpedia\.org\/resource/g)) {
      prefix = 'dbr:';
    }
    return prefix + match[1];
  }

  constructor(public uip: UniqueIdProviderService, public evntService: EventProviderService) {
    this.evntService.detailClickEvent.subscribe((id) => { this.highlightPath(id); });
  }

  ngOnInit() {
    // sort graphData
    this.graphData.piecesOfEvidence.sort(this.pathSorter);
  }

  pathSorter(path1: CgPath, path2: CgPath) {
    const len1 = path1.evidence.length;
    const len2 = path2.evidence.length;
    return ((len1 < len2) ? -1 : ((len1 > len2) ? 1 : 0));
  }

  ngAfterViewInit() {
    this.maxXDist = this.getMaxDist(this.graphData);
    const curScope = this;
    const svg = d3.select('svg');

    const nodeSvg: any = svg.node();
    let width = nodeSvg.clientWidth;
    let height = nodeSvg.clientHeight;
    if (width === 0 || height === 0) {
      width = 500;
      height = 500;
    }
    this.svg = svg;

    this.g = svg.append('g');
    const g = this.g;
    this.tooltipDiv = d3.select('#result-body').append('div')
      .attr('class', 'tooltip')
      .style('opacity', 0)
      .style('height', 'auto');

    this.ttSpan = this.tooltipDiv.append('div').attr('class', 'text-span');

    const eleMap = { };
    this.sNodeY = height / 2;
    this.sNodeX = width / 2;
    this.getDefaultPath(CgTriple.map(this.graphData.fact));
    this.getAllPaths(this.graphData.piecesOfEvidence, CgTriple.map(this.graphData.fact));
    // Sending pathlist to details view (with id added)
    this.sendDetails(this.graphData.piecesOfEvidence);

    this.drawEdges(this.edgeArr);
    this.drawCircles(this.nodeArr);
    this.drawNodeLabels(this.nodeArr);
    this.drawEdgeLables(this.edgeArr);
    // arrows
    svg.append('svg:defs').append('svg:marker')
      .attr('id', 'arrow').attr('viewBox', '0 0 10 10')
      .attr('refX', 0).attr('refY', 5)
      .attr('markerUnits', 'strokeWidth')
      .attr('markerWidth', 7)
      .attr('markerHeight', 2.5)
      .attr('orient', 'auto')
      .append('svg:path')
      .attr('d', 'M 0 0 L 10 5 L 0 10 z');

    const zoom = d3.zoom()
      .scaleExtent([0.1, 10])
      .on('zoom', function() { g.attr('transform', d3.event.transform); });

    svg.call(zoom);

    const midX = (this.sNode.cx + this.eNode.cx) / 2;
    const midY = (this.sNode.cy + this.eNode.cy) / 2;

    const texts = ['Use the scroll to zoom'];

    svg.selectAll('overlay-text')
      .data(texts)
      .enter()
      .append('text')
      .attr('x', 10)
      .attr('y', function(d, i) { return (height - 20) + i * 18; })
      .text(function(d) { return d; })
      .style('fill', 'grey')
      .style('opacity', '0.5')
      .on('click', function(d) {
          d3.select(this).transition()
            .duration(300).style('display', 'none');
      });
  }

  saveToMap(key, valKey, elDt, eleMap) {
    if (!eleMap[key]) {
      eleMap[key] = {};
    }
    const item = eleMap[key];
    item[valKey] = elDt;
  }

  getMaxDist(data: CgData) {
    let maxLen = 1;
    for (const x in this.graphData.piecesOfEvidence) {
      const curLen = this.graphData.piecesOfEvidence[x].evidence.split('>/', 5).length;
      if (curLen > maxLen) {
        maxLen = curLen;
      }
    }
    return maxLen * this.minXDist;
  }
  getDefaultPath(triple: CgTriple) {
    const sNode = new CgNodeItem(this.sNodeX, this.sNodeY, triple.subject);
    this.sNode = sNode;
    const maxDist = this.maxXDist;
    const eNode = new CgNodeItem(this.sNodeX + maxDist, this.sNodeY, triple.object);
    this.eNode = eNode;
    const edge = new CgLineItem(sNode.cx, sNode.cy, eNode.cx, eNode.cy, triple.property, 0.5, this.uip.getUniqueId(), -1);

    this.nodeArr.push(sNode);
    this.nodeArr.push(eNode);
    edge.isDotted = true;
    this.edgeArr.push(edge);
  }

  drawNodeLabels(items: CgNodeItem[]) {
    const fontSz = 20;
    const curScope = this;
    const circLblG = this.g.append('g').attr('id', 'circle-label-svg');
    const label = circLblG.selectAll('text').data(items);
    const labelEnter = label.enter().append('text');
    labelEnter.attr('x', function(d) { if (d.cx === curScope.sNodeX) { return d.cx - curScope.nodeRad - 10; } else if (d.cx === curScope.eNode.cx) { return d.cx + curScope.nodeRad + 10; } else { return d.cx; } });
    labelEnter.attr('y', function(d) { if (d.cy < curScope.sNodeY) {return d.cy - curScope.nodeRad - 8; } else if (d.cy > curScope.sNodeY) {return d.cy + curScope.nodeRad + 2 + fontSz; } else {return d.cy + fontSz / 2; }});
    labelEnter.attr('text-anchor', function(d) { if (d.cx === curScope.sNodeX) { return 'end'; } else if (d.cx === curScope.eNode.cx) { return 'start'; } else { return 'middle'; }});
    //labelEnter.text( function(d) {return GraphViewComponent.getUriName(d.uri); });
    labelEnter.attr('font-size', fontSz);
    labelEnter.attr('class', 'node-lbl');
  }

  drawEdgeLables(items: CgLineItem[]) {
    const curScope = this;
    const pathLblG = this.g.append('g').attr('id', 'path-label-svg');
    const label = pathLblG.selectAll('text').data(items);
    const labelEnter = label.enter().append('text').attr('dy', -10).attr('transform', function(d) {
      if (d.cx1 > d.cx2 && !d.isCurved) {
        // d3.select(this).attr('transform-origin', (d.cx1 + d.cx2) / 2 + ',' + (d.cy1 + d.cy2) / 2 );
         return  'rotate(180 ' + (d.cx1 + d.cx2) / 2 + ',' + (d.cy1 + d.cy2) / 2  + ')';
      }
        if (d.cx1 > d.cx2 && d.isCurved) {
          // d3.select(this).attr('transform-origin', (d.cx1 + d.cx2) / 2 + ',' + (d.cy1 + d.cy2) / 2 );
          return  'rotate(180 ' + d.cpx + ',' + d.cpy + ')';
        }
      return ''; }).append('textPath') // append a textPath to the text element
      .attr('xlink:href', function(d) { return '#' + d.id; }) // place the ID of the path here
      .style('text-anchor', 'middle') // place the text halfway on the arc
      .attr('startOffset', '50%')
      .text(function(d) { return GraphViewComponent.getUriName(d.uri); })
      .attr('class', 'edge-lbl')
      ;

  }

  drawCircles(items: CgNodeItem[]) {
    const nodeCol = 'orange';
    // Define the div for the tooltip
    const curScope = this;
    const circG = this.g.append('g').attr('id', 'circle-svg');
    const circle = circG.selectAll('circle')
      .data(items);
    const circleEnter = circle.enter().append('circle');
    circleEnter.attr('cy', function(d) { return d.cy; });
    circleEnter.attr('cx', function(d) { return d.cx; });
    circleEnter.attr('r', this.nodeRad);
    circleEnter.attr('fill', nodeCol);
    circleEnter.on('mouseover', function(d) {
      curScope.ttOnMouseOver(d, curScope);
      d3.select(this).transition()
        .duration(300).style('fill', '#62a6ff');
    })
      .on('mouseout', function(d) {
       curScope.ttOnMouseOut(d, curScope);
        d3.select(this).transition()
          .duration(300).style('fill', nodeCol);
      });

  }
  ttOnMouseOver(d, curScope) {
    curScope.tooltipDiv.transition()
      .duration(200)
      .style('opacity', .7);
    curScope.tooltipDiv
      .style('left', (d3.event.pageX) + 'px')
      .style('top', (d3.event.pageY - 28) + 'px');
    curScope.ttSpan.html(d.uri);
  }

  ttOnMouseOut(d, curScope) {
    curScope.tooltipDiv.transition()
      .duration(500)
      .style('opacity', 0);
  }

  drawEdges(items: CgLineItem[]) {
    const curScope = this;
    const pathG = this.g.append('g').attr('id', 'path-svg');
    const linesG = pathG.selectAll('.link').data(items)
      .enter().append('g');
    this.linesG = linesG;
    const lines = linesG.append('path')
      .attr('id', function(d) { return d.id; })
      .attr('d', this.getEdgePath)
      .style('fill', 'none')
      .attr('class', 'line-def')
      .style('stroke-width', function(d) { return (d.pathScore + 2) * curScope.pathStrFac + 'px'; })
      .attr('marker-mid', 'url(#arrow)')
      .on('mouseover', function(d) {
      curScope.ttOnMouseOver(d, curScope);

        linesG.selectAll('path').each(function(d1: CgLineItem) { if (d1.pathId === d.pathId && d.id !== d1.id) {
          d3.select(this).transition()
            .duration(300).attr('class', 'line-path-sel');
        } });
        d3.select(this).transition()
          .duration(300).attr('class', 'line-sel');
    })
      .on('mouseout', function(d) {
        curScope.ttOnMouseOut(d, curScope);
        if (curScope.mouseOutTog === d.pathId) {
          return;
        }

        linesG.selectAll('path').each(function(d1: CgLineItem) { if (d1.pathId === d.pathId && d.id !== d1.id) {
          d3.select(this).transition()
            .duration(300).attr('class', 'line-def');
        } });
        d3.select(this).transition()
          .duration(300).attr('class', 'line-def');
      }).on('click', function(d) {

        if (curScope.mouseOutTog === d.pathId) {
          curScope.mouseOutTog = -2;
          linesG.selectAll('path').each(function(d1: CgLineItem) {
            d3.select(this).transition()
              .duration(300).attr('class', 'line-def'); });
        } else {
          curScope.mouseOutTog = d.pathId;
          curScope.evntService.pathClickEvent.emit(d.pathId);
          linesG.selectAll('path').each(function(d1: CgLineItem) {
            if (d1.pathId === d.pathId && d.id !== d1.id) {
              d3.select(this).transition()
                .duration(300).attr('class', 'line-path-sel');
            } else if (d1.pathId !== d.pathId) {
              d3.select(this).transition()
                .duration(300).attr('class', 'line-def');
            }
          });
          d3.select(this).transition()
            .duration(300).attr('class', 'line-sel');
        }
      }).each(function(d) { if (d.isDotted) { d3.select(this).style('stroke-dasharray', ('10, 3')); }});
  }

  getEdgePath(d: CgLineItem) {
    let pathD = '';
    let midX = 0;
    let midY = 0;
    if (d.isCurved) {
      const lineGenerator = d3.line().curve(d3.curveNatural);
      pathD = lineGenerator([[d.cx1, d.cy1], [d.cpx, d.cpy ], [d.cx2, d.cy2]]);
      midX = d.cpx;
      midY = d.cpy;
    } else {
      pathD = ' M ' + d.cx1 + ' , ' + d.cy1 + ' L ' + ( d.cx1 + 2 * d.cx2) / 3 + ' , '
        + ( d.cy1 + 2 * d.cy2) / 3 + ' L ' + d.cx2 + ' , ' + d.cy2;
      midX = ( d.cx1 +  d.cx2) / 2;
      midY = ( d.cy1 + d.cy2) / 2;
    }
    return pathD;
  }

  sendDetails(cgPaths: CgPath[]) {
    this.evntService.sendDetailEvent.emit(cgPaths);
  }

  getAllPaths(cgPaths: CgPath[], factForCheck: CgTriple) {
    let yDelta = this.minYDist;
    // Loop through all the paths
    for (let i = 0; i < cgPaths.length; i++) {
      const pathId = i;
      const curPath = cgPaths[i];
      const evidences = curPath.evidence.split('>/', 4);
      curPath.id = pathId;
      let prevNode = this.sNode;
      const xDelta = this.maxXDist / evidences.length;
      const pathScore = curPath.score;
      // Loop through each triple in a path
      for (let j = 0; j < evidences.length; j++) {

        let curSub = factForCheck.subject;
        if (j > 0) {
          curSub = 'a' + j;
        }
        let curObj = factForCheck.object;
        if (j + 1 !== evidences.length) {
          const tempJ = j + 1;
          curObj = 'a' + tempJ;
        }
        let isReverse = false;
        if (evidences[j][0] === '^' ) {
          isReverse = true;
        }
        const curProp = evidences[j].replace('<', '').replace('>', '').replace('^','');
        const nextNode: CgNodeItem = new CgNodeItem(prevNode.cx + xDelta, this.sNode.cy + yDelta, '');
        const edge: CgLineItem = new CgLineItem(0, 0, 0, 0, curProp, pathScore, this.uip.getUniqueId(), pathId);
        let fromNode: CgNodeItem;
        let toNode: CgNodeItem;
        let tempNode: CgNodeItem;
        if (curSub === prevNode.uri) {
          nextNode.uri = curObj;
          fromNode = prevNode;
          toNode = nextNode;
        } else if (curObj === prevNode.uri) {
          nextNode.uri = curSub;
          fromNode = nextNode;
          toNode = prevNode;
        }

        if ( isReverse ) {
          tempNode = fromNode;
          fromNode = toNode;
          toNode = tempNode;
        }

        if (nextNode.uri === this.eNode.uri) {
          if (prevNode.uri === this.sNode.uri) {
            edge.isCurved = true;
            edge.cpx = prevNode.cx + this.maxXDist / 2;
            edge.cpy = nextNode.cy;
          }
          nextNode.cx = this.eNode.cx;
          nextNode.cy = this.eNode.cy;
        }

        edge.cx1 = fromNode.cx;
        edge.cy1 = fromNode.cy;
        edge.cx2 = toNode.cx;
        edge.cy2 = toNode.cy;

        prevNode = nextNode;
        if (nextNode.uri !== this.eNode.uri) {
          this.nodeArr.push(nextNode);
        }
        this.edgeArr.push(edge);
      }
      // setting the y coordinate for next path
      if (yDelta > 0) {
        yDelta = -yDelta;
      } else {
        yDelta = -yDelta + this.minYDist;
      }
    }
  }

  highlightPath(id: number) {
    this.mouseOutTog = id;
    this.linesG.selectAll('path').attr('class', function(d: CgLineItem) {
      if (d.pathId === id) {
        return 'line-path-sel';
      }
      return 'line-def';
    });
  }

}
